### ip2region
本插件适用于ElasticSearch8， ES8 Ingest Pipeline插件，基于ip2region从IP中提取省份、城市、运营商信息，对相关索引在数据输入时实现信息提取并存储，方便后续检索、统计等。
本插件主要解决官方内置GeoIP处理器无法获取运营商信息、国内很多IP无法获取地理信息、地区信息非汉化等问题，对国内使用不够友好。
[ip2region](https://github.com/lionsoul2014/ip2region)通过数据整合，准确率达到99.99%，是个不错选择。

### 安装
通过Github包安装：
```shell
bin/elasticsearch-plugin install https://github.com/yongplus/ip2region-es-ingest-pipeline/releases/download/1.0/ip2region-plugin.zip
```
或上传包到es服务器
```shell
bin/elasticsearch-plugin install https://github.com/yongplus/ip2region-es-ingest-pipeline/releases/download/1.0/ip2region-plugin.zip
```
`由于`

### 参数
field: ip字段  
target_field：转换的ip信息对象字段，默认regionip  
ignore_missing：默认为false，false文档如果field字段缺少，会抛异常  
properties：返回的属性，数组，默认所有 ["country_name","isp_name", "region_name", "city_name","ip"]  

### 运行
- 创建pipeline
```HTTP
PUT _ingest/pipeline/ip2region
{
  "description" : "Add geoip info",
  "processors" : [
    {
      "ip2region" : {
        "field" : "ip",
        "ignore_missing":true,
        "properties":["country_name","isp_name", "region_name", "city_name"]
      }
    }
  ]
}

```
- 测试pipeline
```
POST _ingest/pipeline/ip2region/_simulate
{
  "docs": [
    {
      "_source": {
        "ip": "37.139.1.1",
        "test": {"a":1,"b":2}
      }
    },
    {
      "_source": {
        "ip": "183.47.12.127"
      }
    }
  ]
}
```
Response:
```
{
  "docs": [
    {
      "doc": {
        "_index": "_index",
        "_version": "-3",
        "_id": "_id",
        "_source": {
          "regionip": {
            "country_name": "荷兰",
            "isp_name": "0",
            "region_name": "阿姆斯特丹",
            "city_name": "阿姆斯特丹"
          },
          "test": {
            "a": 1,
            "b": 2
          },
          "ip": "37.139.1.1"
        },
        "_ingest": {
          "timestamp": "2024-07-16T09:18:11.672130572Z"
        }
      }
    },
    {
      "doc": {
        "_index": "_index",
        "_version": "-3",
        "_id": "_id",
        "_source": {
          "regionip": {
            "country_name": "中国",
            "isp_name": "电信",
            "region_name": "广东省",
            "city_name": "珠海市"
          },
          "ip": "183.47.12.127"
        },
        "_ingest": {
          "timestamp": "2024-07-16T09:18:11.672164543Z"
        }
      }
    }
  ]
}
```


### 其它
- 可以通过index.default_pipeline为索引或索引模板指定ingest pipeline
- [官方Ingest Pipeline文档, 探索更多用法](https://www.elastic.co/guide/en/elasticsearch/reference/current/ingest.html)
- 关于ip库更新及方法，可以关注[ip2region仓库](https://github.com/lionsoul2014/ip2region)，将生成的ip2region.xdb替换es插件目录下的文件并重启es即可


### 我的应用
通过ELK收集各个系统的访问日志，为索引指定了Ingest Pipeline实现ip转地区和运营商信息，对索引的数据进行Visualization，为产品运营决策提供数据支持。

![截图](https://github.com/yongplus/ip2region-es-ingest-pipeline/blob/master/assets/list.png?raw=true)

![截图](https://github.com/yongplus/ip2region-es-ingest-pipeline/blob/master/assets/dashboard.png?raw=true)

