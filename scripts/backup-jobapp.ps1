param(
    [string]$Namespace = "jobapp",
    [string]$BackupRoot = "C:\backups\jobapp",
    [string]$MySqlDatabase = "jobapp_db",
    [string]$MySqlUser = "root",
    [string]$MySqlPassword = "rootpass",
    [string]$MongoUser = "admin",
    [string]$MongoPassword = "adminpass"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$targetDir = Join-Path $BackupRoot $timestamp
$mysqlDir = Join-Path $targetDir "mysql"
$mongoDir = Join-Path $targetDir "mongo"
$k8sDir = Join-Path $targetDir "k8s"

New-Item -ItemType Directory -Force -Path $mysqlDir | Out-Null
New-Item -ItemType Directory -Force -Path $mongoDir | Out-Null
New-Item -ItemType Directory -Force -Path $k8sDir | Out-Null

Write-Host "==> Buscando pods en namespace '$Namespace'..."
$mysqlPod = kubectl get pod -n $Namespace -l app=mysql -o jsonpath='{.items[0].metadata.name}'
$mongoPod = kubectl get pod -n $Namespace -l app.kubernetes.io/name=mongodb -o jsonpath='{.items[0].metadata.name}'

if ([string]::IsNullOrWhiteSpace($mysqlPod)) {
    throw "No se encontro pod MySQL (label app=mysql) en namespace '$Namespace'."
}
if ([string]::IsNullOrWhiteSpace($mongoPod)) {
    throw "No se encontro pod MongoDB (label app.kubernetes.io/name=mongodb) en namespace '$Namespace'."
}

$mysqlPod = $mysqlPod.Trim()
$mongoPod = $mongoPod.Trim()

$mysqlRemote = "/tmp/jobapp_mysql_$timestamp.sql"
$mysqlLocal = Join-Path $mysqlDir "jobapp_mysql_$timestamp.sql"

Write-Host "==> Generando backup MySQL en pod '$mysqlPod'..."
kubectl exec -n $Namespace $mysqlPod -- sh -c "mysqldump -u$MySqlUser -p$MySqlPassword --databases $MySqlDatabase > $mysqlRemote"
kubectl cp "$Namespace/${mysqlPod}:$mysqlRemote" $mysqlLocal
kubectl exec -n $Namespace $mysqlPod -- sh -c "rm -f $mysqlRemote"

$mongoRemote = "/tmp/jobapp_mongo_$timestamp.archive"
$mongoLocal = Join-Path $mongoDir "jobapp_mongo_$timestamp.archive"

Write-Host "==> Generando backup MongoDB en pod '$mongoPod'..."
kubectl exec -n $Namespace $mongoPod -- sh -c "mongodump --uri='mongodb://$MongoUser:$MongoPassword@localhost:27017/admin' --archive=$mongoRemote --gzip"
kubectl cp "$Namespace/${mongoPod}:$mongoRemote" $mongoLocal
kubectl exec -n $Namespace $mongoPod -- sh -c "rm -f $mongoRemote"

Write-Host "==> Exportando estado de recursos Kubernetes..."
kubectl get all,cm,secret,pvc,ingress -n $Namespace -o yaml | Out-File -FilePath (Join-Path $k8sDir "jobapp_state_$timestamp.yaml") -Encoding utf8

Write-Host ""
Write-Host "Backup completado en: $targetDir"
Write-Host "  - MySQL: $mysqlLocal"
Write-Host "  - Mongo: $mongoLocal"
Write-Host "  - K8s:   $(Join-Path $k8sDir "jobapp_state_$timestamp.yaml")"
