# 天猫推荐算法竞赛MapReduce实现

### 项目简介:天猫举办的基于用户的行为日志分析出用户可能喜好商品的赛事
![天猫推荐算法大挑战](https://github.com/hongchazhidao/tianmaoRecommend/blob/master/picture/%E5%A4%A9%E7%8C%AB%E6%8E%A8%E8%8D%90%E7%AE%97%E6%B3%95%E5%A4%A7%E6%8C%91%E6%88%98.jpg)
![天猫推荐算法竞赛简介](https://github.com/hongchazhidao/tianmaoRecommend/blob/master/picture/%E5%A4%A9%E7%8C%AB%E6%8E%A8%E8%8D%90%E7%AE%97%E6%B3%95%E7%AB%9E%E8%B5%9B%E7%AE%80%E4%BB%8B.png)


### 项目内容:
1,搭建hadoop分布式集群,yarn集群

2,通过实现基于物品的协同过滤算法来得出用户可能喜好的商品
      过程包括对原始数据的去重,计算出所有用户对物品的喜好矩阵,物品间的同现矩阵,两个矩阵相乘、同类推荐度累加,去除已购买过的商品(可选),按推荐度排序商品等



### 项目成果:通过对原始数据的分析,最终得出每个用户最可能喜好的前10个商品
天猫推荐算法项目原始数据:

![天猫推荐算法项目原始数据](https://github.com/hongchazhidao/tianmaoRecommend/blob/master/picture/%E5%A4%A9%E7%8C%AB%E6%8E%A8%E8%8D%90%E7%AE%97%E6%B3%95%E9%A1%B9%E7%9B%AE%E5%8E%9F%E5%A7%8B%E6%95%B0%E6%8D%AE.png)

天猫推荐算法项目最终MR输出:

![天猫推荐算法项目最终MR输出](https://github.com/hongchazhidao/tianmaoRecommend/blob/master/picture/%E5%A4%A9%E7%8C%AB%E6%8E%A8%E8%8D%90%E7%AE%97%E6%B3%95%E9%A1%B9%E7%9B%AE%E6%9C%80%E7%BB%88MR%E8%BE%93%E5%87%BA.png)




