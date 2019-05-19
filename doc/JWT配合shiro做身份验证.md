# JWT配合shiro做身份验证

## JWT (Json Web Token)

JWT是一串字符串 里面保存了三部分信息

header ：声明类型和加密算法

payload：存放自定义信息的地方 KV方式

signature：由base64编码的header + "." + payload 再用header中生成的加密方式进行加盐签名 盐值是secret

### 如何使用JWT作为Token

简单来说

   1. 用户成功登录
   2. 生成Token
   3. 返回给用户
   4. 把Token 放到header里(请求头)
   5. 服务端从header里取出Token并验证 如果验证成功则代表用户凭证是有效的 反之则拦截
   
下面将对这几个步骤进行拆解 详细的讲一下具体是怎么运作的

### 详解

> 这需要你了解一些shiro技术 本文不对shiro展开具体描述

#### 0x01 用户登录

这一步是基本的使用username password进行shiro的认证登录

用的UsernamePasswordToken 但我还需要一个secret 可以通过继承的方式拓展它

在PasswordRealm中 具体做了这么几件事情
   - 通过username找到目标用户
   - 把hash的密码，盐值拿出来 设定hash方法和hash次数 交给shiro进行认证登录
   - 把hash的密码拿出来 作为JWT的signature部分的secret

> 为了提高安全性 JWT的secret尽量不要直接使用用户密码

#### 0x02 生成Token

在login方法登录成功之后 
我们需要生成一个Token来辨识用户

既然我们需要辨识用户 那我们就应该把用户的唯一标识拿出来并存放到Token里

由于payload只是使用了base64编码 所以请不要把密码放到payload里

但是你可以使用密码(最好是hash之后的)作为secret对Token签名

#### 0x03 返回给用户

把JWT生成的字符串返回出去即可

> 一个标准就是 在token之前添加`Bearer `

在这之后 客户端需要把你返回的Token放到header里 在这之后的每个请求里都需要在请求头里携带该Token

> header的key 可以是 `Authorization`

#### 0x04

writing

#### 0x05

writing