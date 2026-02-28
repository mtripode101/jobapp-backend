param(
    [string]$Namespace = "jobapp",
    [string]$BackupRoot = "C:\backups\jobapp",
    [string]$BackupFolder = "",
    [string]$MySqlDatabase = "jobapp_db",
    [string]$MySqlUser = "root",
    [string]$MySqlPassword = "rootpass",
    [string]$MongoUser = "admin",
    [string]$MongoPassword = "adminpass",
    [switch]$SkipMySql,
    [switch]$SkipMongo
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Get-LatestBackupFolder([string]$root) {
    $dirs = Get-ChildItem -Path $root -Directory | Sort-Object Name -Descending
    if (-not $dirs -or $dirs.Count -eq 0) {
        throw "No se encontraron carpetas de backup en '$root'."
    }
    return $dirs[0].FullName
}

if ([string]::IsNullOrWhiteSpace($BackupFolder)) {
    $BackupFolder = Get-LatestBackupFolder -root $BackupRoot
}

if (-not (Test-Path $BackupFolder)) {
    throw "La carpeta de backup no existe: $BackupFolder"
}

Write-Host "==> Usando backup: $BackupFolder"

$mysqlPod = kubectl get pod -n $Namespace -l app=mysql -o jsonpath='{.items[0].metadata.name}'
$mongoPod = kubectl get pod -n $Namespace -l app.kubernetes.io/name=mongodb -o jsonpath='{.items[0].metadata.name}'

if ([string]::IsNullOrWhiteSpace($mysqlPod) -and -not $SkipMySql) {
    throw "No se encontro pod MySQL (label app=mysql) en namespace '$Namespace'."
}
if ([string]::IsNullOrWhiteSpace($mongoPod) -and -not $SkipMongo) {
    throw "No se encontro pod MongoDB (label app.kubernetes.io/name=mongodb) en namespace '$Namespace'."
}

$mysqlPod = $mysqlPod.Trim()
$mongoPod = $mongoPod.Trim()

if (-not $SkipMySql) {
    $mysqlBackupFile = Get-ChildItem -Path (Join-Path $BackupFolder "mysql") -File -Filter "*.sql" | Sort-Object Name -Descending | Select-Object -First 1
    if (-not $mysqlBackupFile) {
        throw "No se encontro archivo .sql en '$BackupFolder\mysql'."
    }

    Write-Host "==> Restaurando MySQL desde '$($mysqlBackupFile.FullName)'..."
    $remoteMySqlFile = "/tmp/restore_mysql.sql"
    kubectl cp $mysqlBackupFile.FullName "$Namespace/${mysqlPod}:$remoteMySqlFile"
    kubectl exec -n $Namespace $mysqlPod -- sh -c "mysql --binary-mode=1 -u$MySqlUser -p$MySqlPassword $MySqlDatabase < $remoteMySqlFile"
    kubectl exec -n $Namespace $mysqlPod -- sh -c "rm -f $remoteMySqlFile"
}

if (-not $SkipMongo) {
    $mongoBackupFile = Get-ChildItem -Path (Join-Path $BackupFolder "mongo") -File -Filter "*.archive" | Sort-Object Name -Descending | Select-Object -First 1
    if (-not $mongoBackupFile) {
        throw "No se encontro archivo .archive en '$BackupFolder\mongo'."
    }

    Write-Host "==> Restaurando MongoDB desde '$($mongoBackupFile.FullName)'..."
    $remoteMongoFile = "/tmp/restore_mongo.archive"
    kubectl cp $mongoBackupFile.FullName "$Namespace/${mongoPod}:$remoteMongoFile"
    kubectl exec -n $Namespace $mongoPod -- sh -c "mongorestore --uri='mongodb://$MongoUser:$MongoPassword@localhost:27017/admin' --archive=$remoteMongoFile --gzip --drop"
    kubectl exec -n $Namespace $mongoPod -- sh -c "rm -f $remoteMongoFile"
}

Write-Host ""
Write-Host "Restore completado."
