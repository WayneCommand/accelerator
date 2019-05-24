# shiro多Realm认证器

### 最初的问题
起初是在写多种方式认证登录的时候 遇到了多realm报错的问题

业务是这么一回事：

最开始的时候是用用户名+密码进行登录认证的

现在要新加一个验证方式 手机号+短信验证码登录

其实很简单对吧 只需要写一个Token 短信验证的Realm 这样就ok了

shiro会自动调用多realm认证 并且通过supports来判断该Token是否支持当前realm的认证

这一切看起来都很顺利 对吧

直到 shiro new出了一个AuthenticationException

业务逻辑 乱掉了...

### 问题分析

**为什么我捕捉到的异常不是我想要的异常**

为什么我之前捕捉的IncorrectCredentialsException&UnknownAccountException&.. 现在都捕捉不到了

取而代之的是一个AuthenticationException

这要先从多Realm的认证策略讲起

shiro的AbstractAuthenticationStrategy下的认证策略有三种

 - AllSuccessfulStrategy
 > 全部验证成功了 才算成功
 
 - AtLeastOneSuccessfulStrategy
 > 至少一个验证成功 会聚合返回数据
 
 - FirstSuccessfulStrategy
 > 第一个验证成功了就算成功 剩下的不管了
 
知道了这三种验证策略 我们不妨想一下这种情况 在不同的认证策略是怎么处理的

我有两个realm 在认证的过程中 两个realm抛出了不同的异常

**AllSuccessfulStrategy**
在第一个realm中 抛出异常 策略立即捕获 返回

**AtLeastOneSuccessfulStrategy**
在第一个realm中 抛出异常 策略捕获 进行下一个realm认证
直到最后一个realm认证完毕 聚合数据 返回

**FirstSuccessfulStrategy**
在第一个realm中 抛出异常 策略捕获 进行下一个realm认证
直到有一个realm认证完毕 然后返回