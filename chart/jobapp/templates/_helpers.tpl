#_helpers.tpl
{{- define "jobapp.name" -}}
jobapp
{{- end -}}

{{- define "jobapp.fullname" -}}
{{ include "jobapp.name" . }}
{{- end -}}