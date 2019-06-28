DDoS Attack Detection 
---

This project implements a simple DDoS attack detection using Spark. It does not
use any Machine Learning library.


### Pre-requisite
1. sbt
2. scala 2.11
3. spark 2.2+

### Running

spark-submit --master local[2] --class com.phdata.ddos.LogMonitor




### Building
---
```
 sbt compile assembly

```
