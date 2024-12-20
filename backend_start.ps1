﻿$global:p = @();
try
{
    Write-Output "Building common"
    mvn -q -f backend/common/pom.xml clean install "-Dmaven.test.skip=true"

    $arguments = $args -join " "
    Get-ChildItem -Directory -Path ".\backend" | ForEach-Object {
        $directory = $_.FullName
        if ($_.Name -ne "common")
        {
            Write-Output "Building $( $_.Name )"
            $global:p += Start-Job { mvn -q -f "$using:directory/pom.xml" clean compile exec:java "-Dmaven.test.skip=true" "-Dexec.args=$( $using:arguments )" }
        }
    }

    $global:p | Receive-Job -Wait -AutoRemoveJob
}
finally
{
    Write-Output "Stopping all processes" | out-host
    $global:p | Remove-Job -Force
    exit
}