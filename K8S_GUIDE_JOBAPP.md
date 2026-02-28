# Guia Completa: JobApp en Kubernetes (Minikube)

Esta guia resume todo lo que hicimos para levantar el stack completo en Kubernetes y explica el motivo de cada paso.

## 1. Objetivo

Levantar en `minikube` los 3 proyectos:

- `jobapp-frontend`
- `jobapp_backend`
- `mongodb` (servicio de notas + MongoDB)

con acceso final por Ingress en:

- `http://jobapp.local`

## 2. Arquitectura final

- `frontend` (React + Nginx) en puerto interno `80`
- `backend` (Spring Boot) en puerto `8080`
- `mysql` (MySQL 8) en puerto `3306`
- `mongodb` (Helm chart Bitnami) en puerto `27017`
- `mongodb-backend` (Spring Boot de notas) en puerto `8080`
- `mongo-express` (opcional) en puerto `8081`
- `Ingress NGINX` enruta:
  - `/` -> `frontend`
  - `/api` -> `backend`
  - `/notes` -> `mongodb-backend`

## 3. Prerrequisitos

- Docker Desktop funcionando
- Minikube instalado
- Kubectl instalado
- Helm instalado

Verificaciones:

```powershell
minikube version
kubectl version --client
helm version
```

## 4. Arranque del cluster

```powershell
minikube start
kubectl config use-context minikube
kubectl get nodes
```

Habilitar Ingress:

```powershell
minikube addons enable ingress
kubectl -n ingress-nginx get pods
```

Esperar controller listo:

```powershell
kubectl -n ingress-nginx wait --for=condition=ready pod -l app.kubernetes.io/component=controller --timeout=180s
```

## 5. Namespace

```powershell
kubectl create namespace jobapp
```

Si ya existe:

```powershell
# no es error bloqueante
```

## 6. Problema en Windows con `minikube image load` (wmic)

Tuvimos error por `wmic` no disponible. Solucion:
build de imagenes directamente en el daemon Docker de Minikube.

```powershell
minikube -p minikube docker-env --shell powershell | Invoke-Expression

docker build -t jobapp-backend:local C:\vstudio\jobapp_backend
docker build -t jobapp-frontend:local C:\vstudio\jobapp-frontend
docker build -t jobapp-mongodb-backend:local C:\vstudio\mongodb
```

Para volver al Docker normal:

```powershell
minikube -p minikube docker-env --shell powershell --unset | Invoke-Expression
```

## 7. Cambios de configuracion que aplicamos

## 7.1 Frontend

Archivo:
- `jobapp-frontend/k8s/base/configmap.yaml`

Cambio:
- agregar `API_UPSTREAM: "http://backend:8080"`

Motivo:
- el `docker-entrypoint.sh` del frontend usa `set -eu` + `envsubst` y requiere `API_UPSTREAM`.

## 7.2 Backend

Archivos:
- `jobapp_backend/k8s/base/configmap.yaml`
- `jobapp_backend/k8s/base/deployment.yaml`

Cambios:
- usar `SPRING_DATASOURCE_URL` real
- mapear credenciales como:
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
- desactivar dependencia de health Redis para entorno K8s local:
  - `SPRING_CACHE_TYPE: "simple"`
  - `MANAGEMENT_HEALTH_REDIS_ENABLED: "false"`
- probes mas tolerantes y con `startupProbe` para backend

Motivo:
- backend caia por `UnknownHost redis-master` y probes tempranos.

## 7.3 MySQL (backend infra)

Archivo:
- `jobapp_backend/k8s/infra/mysql/deployment.yaml`

Cambios:
- agregar `startupProbe` TCP
- aumentar `initialDelaySeconds`/`periodSeconds`/`failureThreshold` en readiness/liveness

Motivo:
- MySQL tardaba en inicializar y liveness lo reiniciaba antes.

## 7.4 MongoDB backend (servicio de notas)

Archivos:
- `mongodb/k8s/apps/base/mongodb-backend/configmap.yaml`
- `mongodb/k8s/apps/base/mongodb-backend/deployment.yaml`
- `mongodb/src/main/resources/application.properties`
- `mongodb/src/main/resources/application-dev.properties`

Cambios:
- URI via `SPRING_DATA_MONGODB_URI`
- probes del deployment cambiados a `tcpSocket: 8080` (no HTTP `/actuator/health`)

Motivo:
- el startup/readiness probe HTTP devolvia `401` en actuator.

## 7.5 MongoDB Helm values

Archivo:
- `mongodb/k8s/helm/mongodb-values.yaml`

Cambios:
- ajustes para minikube:
  - readiness/liveness/startup mas tolerantes
  - recursos mas bajos

Motivo:
- evitar timeouts de probes en entorno local limitado.

## 7.6 Overlay local de mongodb

Archivo:
- `mongodb/k8s/apps/overlays/local/kustomization.yaml`

Cambio:
- incluir `../../base/mongo-express`

## 8. Deploy completo (orden recomendado)

## 8.1 MongoDB (Helm)

```powershell
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
helm upgrade --install mongodb bitnami/mongodb -n jobapp -f C:\vstudio\mongodb\k8s\helm\mongodb-values.yaml
```

## 8.2 Aplicar manifests Kustomize

```powershell
kubectl apply -k C:\vstudio\jobapp_backend\k8s\infra\mysql
kubectl apply -k C:\vstudio\mongodb\k8s\apps\overlays\local
kubectl apply -k C:\vstudio\jobapp_backend\k8s\overlays\local
kubectl apply -k C:\vstudio\jobapp-frontend\k8s\overlays\local
kubectl apply -k C:\vstudio\jobapp-frontend\k8s\ingress
```

## 8.3 Reinicios de rollout (si hace falta)

```powershell
kubectl rollout restart deploy/mysql -n jobapp
kubectl rollout restart deploy/mongodb-backend -n jobapp
kubectl rollout restart deploy/backend -n jobapp
kubectl rollout restart deploy/frontend -n jobapp
```

## 9. Verificacion operativa

Pods:

```powershell
kubectl get pods -n jobapp
```

Estado esperado final:
- todos `1/1 Running`

Servicios:

```powershell
kubectl get svc -n jobapp
kubectl get ingress -n jobapp
```

## 10. Exposicion por dominio local

Levantar tunnel (dejar terminal abierta):

```powershell
minikube tunnel
```

En hosts de Windows (`C:\Windows\System32\drivers\etc\hosts`):

```txt
127.0.0.1 jobapp.local
```

Probar:

- `http://jobapp.local`

## 11. Troubleshooting rapido (comandos utiles)

Eventos recientes:

```powershell
kubectl get events -n jobapp --sort-by=.lastTimestamp | Select-Object -Last 40
```

Logs:

```powershell
kubectl logs -n jobapp deploy/backend --tail=200
kubectl logs -n jobapp deploy/backend --previous --tail=200
kubectl logs -n jobapp deploy/mongodb-backend --tail=200
kubectl logs -n jobapp deploy/mysql --tail=200
```

Describe pod:

```powershell
kubectl describe pod -n jobapp <pod-name>
```

Rollout:

```powershell
kubectl rollout status deploy/backend -n jobapp --timeout=300s
kubectl rollout undo deploy/mongodb -n jobapp
```

Escalado temporal para aislar problemas:

```powershell
kubectl scale deploy/backend -n jobapp --replicas=0
kubectl scale deploy/mongodb-backend -n jobapp --replicas=0
```

## 12. Limpieza / reset completo

```powershell
kubectl delete namespace jobapp
kubectl wait --for=delete ns/jobapp --timeout=180s
kubectl create namespace jobapp
```

Luego repetir deploy desde seccion 8.

## 13. Nota sobre warning de Kustomize

Varios overlays muestran:

- `patchesStrategicMerge is deprecated`

No bloquea funcionamiento. Pendiente recomendado:
- migrar a `patches` en `kustomization.yaml`.

---

Si queres, en un siguiente paso te preparo:

- una version "quickstart" de esta guia (10 comandos maximo)
- scripts PowerShell (`up.ps1`, `down.ps1`, `status.ps1`) para automatizar todo.
