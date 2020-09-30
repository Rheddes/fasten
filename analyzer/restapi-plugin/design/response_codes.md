# API response codes for Maven

## Packages and package versions

| Resource | Method | Req. body | Query params | Status | Description |
|-|-|-|-|-|-|
| `/mvn/packages/{pkg}/versions` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | Package versions returned |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| `/mvn/packages/{pkg}/{pkg_ver}` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| `/mvn/packages/{pkg}/{pkg_ver}/metadata` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| `/mvn/packages/{pkg}/{pkg_ver}/vulnerabilities` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| `/mvn/packages/{pkg}/{pkg_ver}/callgraph` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| `/mvn/packages/{pkg}/{pkg_ver}/callgraph` | `GET` | — | `limit`: `integer`, `offset`: `integer`, `resolve`: `dateTime`, `full`: `boolean` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 400 | Invalid timestamp |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |

## Dependencies

| Resource | Method | Req. body | Query params | Status | Description |
|-|-|-|-|-|-|
| `/mvn/packages/{pkg}/{pkg_ver}/deps` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| `/mvn/packages/{pkg}/{pkg_ver}/deps` | `GET` | — | `limit`: `integer`, `offset`: `integer`, `resolve`: `dateTime` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 400 | Invalid timestamp |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |

## Modules

| Resource | Method | Req. body | Query params | Status | Description |
|-|-|-|-|-|-|
| `/mvn/packages/{pkg}/{pkg_ver}/modules` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| `/mvn/packages/{pkg}/{pkg_ver}/modules/{namespace}/metadata` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| | | | | 404 | Module namespace not found |
| `/mvn/packages/{pkg}/{pkg_ver}/modules/{namespace}/files` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| | | | | 404 | Module namespace not found |

## Binary Modules

| Resource | Method | Req. body | Query params | Status | Description |
|-|-|-|-|-|-|
| `/mvn/packages/{pkg}/{pkg_ver}/binary-modules` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| `/mvn/packages/{pkg}/{pkg_ver}/binary-modules/{binary}/metadata` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| | | | | 404 | Binary name not found |
| `/mvn/packages/{pkg}/{pkg_ver}/binary-modules/{binary}/files` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| | | | | 404 | Binary name not found |

## Callables

| Resource | Method | Req. body | Query params | Status | Description |
|-|-|-|-|-|-|
| `/mvn/packages/{pkg}/{pkg_ver}/callables` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| `/mvn/packages/{pkg}/{pkg_ver}/{fasten_uri}/vulnerabilities` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| | | | | 404 | FASTEN URI not found |
| `/mvn/packages/{pkg}/{pkg_ver}/{fasten_uri}/metadata` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| | | | | 404 | FASTEN URI not found |
| `/mvn/packages/{pkg}/{pkg_ver}/{fasten_uri}/reach` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |
| | | | | 404 | FASTEN URI not found |

## Edges

| Resource | Method | Req. body | Query params | Status | Description |
|-|-|-|-|-|-|
| `/mvn/packages/{pkg}/{pkg_ver}/edges` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |

## Files

| Resource | Method | Req. body | Query params | Status | Description |
|-|-|-|-|-|-|
| `/mvn/packages/{pkg}/{pkg_ver}/files` | `GET` | — | `limit`: `integer`, `offset`: `integer` | 200 | OK |
| | | | | 400 | Invalid limit value |
| | | | | 400 | Invalid offset value |
| | | | | 404 | Package not found |
| | | | | 404 | Package version not found |