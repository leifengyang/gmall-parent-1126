package com.atguigu.gmall.model.list;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;


//Index、Type、Document、、Mapping、Field
//Index索引：动：指给es保存一个数据（索引一个数据）  名：索引库的名（mysql数据的库）
//Type类型： mysql下表， 在es的索引下，区分不同的数据类型。 没用了
//Document文档： es中的一条数据 （mysql的一条记录）
//Mapping 映射:  以前mysql建表需要有表的字段数据类型声明。
//          es中存文档，最好也声明下文档内部每个字段数据的类型；Mapping自动决定



// Index = goods , Type = info  es 7.8.0 逐渐淡化type！  修改！
@Data
@Document(indexName = "goods" , shards = 3,replicas = 2)
public class Goods {
    // 商品Id skuId
    @Id
    private Long id;

    //如果给es存一个字符串，字符串会被当成文本。文本检索会分词
    // 张三  李四 李五
    //检索 李四
    @Field(type = FieldType.Keyword, index = false) //此字段保存的时候不要分词，查的时候也不要分词查，精确匹配
    private String defaultImg; //不是文本，是Keyword。index = false 不要给他建立倒排索引。这个字段不会用来作为查询条件。只是存起来而已

    //  es 中能分词的字段，这个字段数据类型必须是 text！keyword 不分词！
    @Field(type = FieldType.Text, analyzer = "ik_max_word") //es安装ik分词器
    private String title; //商品标题

    @Field(type = FieldType.Double)
    private Double price;

    //  @Field(type = FieldType.Date)   6.8.1
    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime; // 新品

    @Field(type = FieldType.Long)
    private Long tmId; //品牌id

    @Field(type = FieldType.Keyword) //存储的时候不分词
    private String tmName;

    @Field(type = FieldType.Keyword,index = false) //存储的时候不分词
    private String tmLogoUrl;


    //三级分类信息
    @Field(type = FieldType.Long)
    private Long category1Id; //

    @Field(type = FieldType.Keyword)
    private String category1Name;

    @Field(type = FieldType.Long)
    private Long category2Id;

    @Field(type = FieldType.Keyword)
    private String category2Name;

    @Field(type = FieldType.Long)
    private Long category3Id;

    @Field(type = FieldType.Keyword)
    private String category3Name;

    //  商品的热度！ 我们将商品被用户点查看的次数越多，则说明热度就越高！
    @Field(type = FieldType.Long)
    private Long hotScore = 0L;  //

    // 平台属性集合对象
    // Nested 支持嵌套查询
    @Field(type = FieldType.Nested) //嵌入式的对象
    private List<SearchAttr> attrs; //平台属性； 要用来检索； 集合类型如果不说Nested就会导致错误的检索结果
    //48
    // 5:机身存储： 128GB
    // 6:运行内存: 8GB
    //49
    // 5:机身存储： 256GB
    // 6:运行内存: 8GB

}
