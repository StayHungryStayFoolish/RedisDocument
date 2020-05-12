# Redis 

## Redis Cluster Install

-   Env

    -   Mac OS
    -   Docker 

-   Install

    -   docker 文件下执行 `docker-compose build`,然后执行 `docker-compose up`.

        `* Background AOF rewrite finished successfully` 表示集群启动成功.

-   Link

    [Mac上最简单明了的利用Docker搭建Redis集群](https://juejin.im/post/5cbd3c435188250a8b7cf55e)       

## Lettuce 

[Lettuce](https://lettuce.io/core/release/reference/#overview)

[Lettuce GitHub](https://github.com/lettuce-io/lettuce-core/wiki/About-Lettuce)

## Redis 数据结构

1.  **String**

    -   **二进制安全**
        -   计算机存储的最小单位是 `Bit（比特，也成位）`，一个`Byte（字节）`。1 Byte = 8 bit。每个 Bit 用 `0` 或 `1` 来表示。`Character（字符）`通过不同的字符编码（ASCII、Unicode、UTF-8 、GBK等）由其指定固定的字节来表示。
        -   `二进制安全` 是一个`输入`和`输出`以`字节`为单位的流，当数据`输入`时，不会对数据进行任何`限制`、`过滤`等（*例如：C 语言使用长度 N + 1 的方式表示字符串，N 为字符串，1 表示字符数组最后一个元素以'\0' 结尾，当读取到以 '\0' 结尾时结束*）。即无论`字符`以任何编码形式，最终存储的只是`字节`，保证了`输入`时的原始数据。存储和读取的双方只需约定好编码集就可以获取数据内容，具有跨平台、跨语言、防止数据类型溢出等优点。
    -   **Redis 的 SDS**
        -   

    -   [x] 字符串 

    -   [x] 数值

    -   [x] 二进制位（BitMap）

        ```bash
        SETBIT key offset value
        ```

        -   参数解释
            -   key 存储的 key
            -   offset 偏移量，从高位到低位偏移，即`从左到右`偏移
            -   value 命令是 `bit` 操作，所以 `value` 只能是 `0` 或者 `1`
        -   指令原理
            -   

        ```bash
        127.0.0.1:6379> SETBIT k1 7 1
        (integer) 0
        127.0.0.1:6379> SETBIT k1 7 0
        (integer) 1
        127.0.0.1:6379> get k1
        "\x00"
        ```

2.  **Hash**

3.  **List**

4.  **Set**

5.  **ZSet(Sorted Set)**
