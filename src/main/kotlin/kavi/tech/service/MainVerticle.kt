package kavi.tech.service

import io.vertx.serviceproxy.ServiceBinder
import kavi.tech.service.ms.UserService
import kavi.tech.service.ms.UserServiceImpl
import kavi.tech.service.web.WebVerticle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import tech.kavi.vs.web.HandlerRequestAnnotationBeanName
import tech.kavi.vs.core.LauncherVerticle
import tech.kavi.vs.mybatis.MybatisDataSourceBean

/**
 * 注: 如果不使用 MybatisDataSourceBean 请将JAVA相关注解删除，否则会报依赖错误
 * 如：java包下的 service 和 http的 handler 注解删除
 * */
@Import(BeanConfig::class, MybatisDataSourceBean::class)
@ComponentScan(nameGenerator = HandlerRequestAnnotationBeanName::class)
class MainVerticle : LauncherVerticle() {


    @Autowired
    private lateinit var webVerticle: WebVerticle

    @Autowired
    private lateinit var userService: UserServiceImpl

    @Throws(Exception::class)
    override fun start() {
        super.start()
        vertx.deployVerticle(webVerticle)
        /**
         * 开启用户查询服务
         * */
        ServiceBinder(vertx).setAddress(UserService.Factory.address).register(UserService::class.java, userService)
    }

    companion object {
        @JvmStatic
        fun main(args:Array<String>) {
            // 初始化类
            launcher(MainVerticle::class.java)
        }
    }
}
