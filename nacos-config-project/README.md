### nacos-config-project 使用Nacos做服务配置中心


#### 下载Nacos

**从 Github 上下载源码方式**
```json
git clone https://github.com/alibaba/nacos.git
cd nacos/
mvn -Prelease-nacos -Dmaven.test.skip=true clean install -U  
ls -al distribution/target/

// change the $version to your actual path
cd distribution/target/nacos-server-$version/nacos/bin
```
**下载编译后压缩包方式**

您可以从 <a href="https://github.com/alibaba/nacos/releases">最新稳定版本</a> 下载 nacos-server-$version.zip 包。
```json
  unzip nacos-server-$version.zip 或者 tar -xvf nacos-server-$version.tar.gz
  cd nacos/bin
```
#### 启动服务器

**Linux/Unix/Mac**
启动命令(standalone代表着单机模式运行，非集群模式):

`sh startup.sh -m standalone`

如果您使用的是ubuntu系统，或者运行脚本报错提示[[符号找不到，可尝试如下运行：

`bash startup.sh -m standalone`

**Windows**
启动命令：

`cmd startup.cmd`

或者双击startup.cmd运行文件。

