package com.tf.sc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.tf.sc.mapper")
@EnableScheduling
public class KsApplication {
    public static void main(String[] args) {
        SpringApplication.run(KsApplication.class, args);
    }
}
//         _oo0oo_
//        o8888888o
//        88" . "88
//        (| ^_^ |)
//        O\  =  /O
//     ____/`----'\____
//  .'  \\         //  `.
// /    \\||| : |||//    \
// / _ _|||||-:-|||||- _\
//|  | \\\    -   /// |   |
//| \_|  ''\---/''  |_/ |
//\  .-\__ `-` __/-.  /
//____`. .' /--.--\ `. .'____
//."" '< `.___\<|>___.' >' "".
//| | : `- \.;`;._ /`;./ -' : | |
//\ \ `-.  \_ __\/ __/.-' / /
//======`-.____`\/____.-'======
//        `----='
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//佛祖保佑        永不宕机        永无BUG
    