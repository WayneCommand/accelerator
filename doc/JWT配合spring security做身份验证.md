# JWT配合 spring security 做身份验证

## JWT (Json Web Token)

JWT是一串字符串 里面保存了三部分信息

header：声明类型和加密算法

payload：存放自定义信息的地方(需要放一些用户相关的信息 比如用户名，角色权限) KV方式

signature：由base64编码的header + "." + payload 再用header中生成的加密方式进行加盐签名 盐值是secret

### 如何使用JWT作为Token

简单来说

   1. 用户成功登录
   2. 生成Token
   3. 返回给用户 (放到header里)
   4. 服务端从header里取出Token并验证 如果验证成功则代表用户凭证是有效的 反之则拦截
   
下面将对这几个步骤进行拆解 详细的讲一下具体是怎么运作的

### 详解

> 这需要你了解 spring security, 本文不对 spring security 展开具体描述.

#### 0x01 用户登录

在基于webflux的spring security，你需要写一个WebFilter

对没错 我们需要一个filter来处理用户登录

这个类需要做几件事
 - 判断登录地址是否是`/login`
 - 提取出请求参数 创建登陆用的token `UsernamePasswordAuthenticationToken`
 - 接着交给`ReactiveAuthenticationManager`开始认证流程
 - 流程结束之后 根据结果回调对应的方法success/failure

#### 0x02 生成Token

在login方法登录成功之后 
我们需要生成一个Token来辨识用户

既然我们需要辨识用户 那我们就应该把用户的唯一标识拿出来并存放到Token里

由于payload只是使用了base64编码 所以请不要把密码放到payload里

在`JwtAuthWebFilter` 里成功之后会调用`onAuthenticationSuccess`

这个里面做了这么几件事
1. 创建了个`SecurityContext` 把认证成功的`authentication`放进了context里，并且保存到`ReactiveSecurityContextHolder`里。
2. 然后把这个context存放到`ServerSecurityContextRepository`里(下次请求来的时候就可以根据从这个repository里直接取出context了)。
3. 接着调用你自己写的`authenticationSuccessHandler`。

那么看来第二步就很重要，所以jwt token的生成就在第二步做。

我们就来写一个repository就好了

实现这个repository需要写两个方法，save/load。

在**save**方法中 需要做这么几件事情
 - 判断context参数 如果是null 说明用户做了登出操作 需要失效token
 - 如果不是null 说明是用户登录 需要生成token并保存context
 - 保存的时候因为已经有了完整的用户信息 所以你可以直接生成jwt token。
 - 生成完token之后，把token set 到 exchange 的 attr里面 让第三步来具体处理怎么返回给用户。
 
而**load**方法 则非常简单了

校验token，返回context即可。



#### 0x03 返回给用户

把JWT生成的字符串返回出去即可

但是这个返回机制就需要你自己来仔细考虑了，因为在spring security可以看到使用的机制非常复杂，并且不容易进行定制。

所以这里我的做法是直接由我定义的`authenticationSuccessHandler`来处理返回token

从上一步放进exchange里的attr取出 然后放进header里 设置响应请求。

在这之后 客户端需要把你返回的Token存下来 后续的每个请求里都需要在请求头里携带该Token

> header的key 可以是 `Authorization` 也可以是 自定义的

#### 0x04

在第二步中 我们实现了repository的load方法 可以从请求里取出token 并返回context

那么是谁来操作的这个laod方法呢？

我们再看一个WebFilter `ReactorContextWebFilter`,
这个filter所做的事情就是从repository里load出context并放到`ReactiveSecurityContextHolder`。

如果在repository里验证成功 并返回了context 那么到controller之后 该请求就是认证过的。

ok 似乎搞明白了。但是好像哪里不对。

#### 0x00 e

我们回到起点 想一下`ReactiveAuthenticationManager`是怎么从数据库里拿到用户信息的呢？

这里就要说一个东西`ReactiveUserDetailsService` 这个service就是用来载入用户数据的

我们第一步的filter还需要一个`ReactiveAuthenticationManager`实例 这里就直接使用spring security已经写好的 `UserDetailsRepositoryReactiveAuthenticationManager`

点进源码看一下 他的父类是`AbstractUserDetailsReactiveAuthenticationManager` 超类是 `ReactiveAuthenticationManager`， ok 就是你了。

---
ok 闭环 整个流程就结束了。