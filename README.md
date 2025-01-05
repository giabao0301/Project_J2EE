![UIT](https://camo.githubusercontent.com/fe8ccf76dbe56d6e4e677f6414e9a21e98d9801b823963821c56e66d42614af3/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f66726f6d2d554954253230564e5548434d2d626c75653f7374796c653d666f722d7468652d6261646765266c696e6b3d68747470732533412532462532467777772e7569742e6564752e766e253246)
# AppleMart

### How to run:

Create docker network
```commandline
docker network create -d bridge spring
```
```commandline
docker network create -d bridge mysql
```

Run infra
```commandline
docker compose -f docker-compose.infra.yml up -d
```
Run all services
```commandline
docker compose -f docker-compose.yml up -d
```
