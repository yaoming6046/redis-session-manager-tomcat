刚开始学习使用git,不足处请包涵

本项目主要是使用redis接管tomcat的session
项目的jar包采用maven管理

配置:
将%tomcat_home%/conf/context.xml替换掉
配置文件在项目的resources目录下


session采用redis的hash结构保存.
项目中可以配置是否使用默认session,如果使用默认session,则在session查找的时候优先在默认session中查找.(默认)
如果没有使用默认session,则session的任何操作都是基于redis


由于需要,暂时session的属性值都必须采用字符串形式,如果需要直接保存对象,将字符串判断干掉,并将对想序列化即可.



