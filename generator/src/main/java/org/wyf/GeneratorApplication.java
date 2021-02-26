package org.wyf;

import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.wyf.generator.MyGenerator;
import org.wyf.generator.MyGenerator;

import java.util.ArrayList;
import java.util.List;

public class GeneratorApplication {
    /** 生成地址 */
    private static String PROJECT_PATH = "D://电动自行车自动生成";
    /** 作者 */
    private static String AUTHOR = "zyh";
    /** 数据库 */
    private static String DB_URL = "jdbc:mysql://192.168.0.52:3306/honghu_bc?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&useServerPrepStmts=false&rewriteBatchedStatements=true&allowMultiQueries=true&serverTimezone=UTC";
    private static String DB_SCHEMA_NAME = "honghu_bc";
    private static String DB_DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    private static String DB_USER_NAME = "root";
    private static String DB_PASSWORD = "123456";


    /** 包路径  */
    private static String PARENT_PACKAGE =  "org.wyf";
    /** 功能模块名 */
    private static String MODULE_NAME =  "basic";
    /** 要自动生成的表 */
    private static String [] TABLES = {"t_basic_station"};
    /** 要去除的前缀  */
    private static String TABLE_PREFIX =  "t_";
    /**
     * <p>
     * 读取控制台内容
     * </p>
     */

    public static GlobalConfig gc(){
        GlobalConfig gc = new GlobalConfig();
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(true);
        gc.setOutputDir(PROJECT_PATH);
        gc.setAuthor(AUTHOR);
        gc.setOpen(false);
        return gc;
    }
    public static DataSourceConfig dsc(){

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(DB_URL);
        dataSourceConfig.setSchemaName(DB_SCHEMA_NAME);
        dataSourceConfig.setDriverName(DB_DRIVER_NAME);
        dataSourceConfig.setUsername(DB_USER_NAME);
        dataSourceConfig.setPassword(DB_PASSWORD);
        return dataSourceConfig;
    }
    public static void main(String[] args) {


        // 代码生成器
        MyGenerator mpg = new MyGenerator();

        // 全局配置

        mpg.setGlobalConfig(gc());

        // 数据源配置

        mpg.setDataSource(dsc());

        // 包配置

        PackageConfig pc = new PackageConfig();
        pc.setModuleName(MODULE_NAME);
        pc.setParent(PARENT_PACKAGE);
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };


        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();


        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
         String entity = "/templates/entity.java.vm";
         String service = "/templates/service.java.vm";
         String serviceImpl = "/templates/serviceImpl.java.vm";
         String mapper = "/templates/mapper.java.vm";
         String xml = "/templates/mapper.xml.vm";
         String controller = "/templates/controller.java.vm";
        templateConfig.setEntity(entity);
        templateConfig.setService(service);
        templateConfig.setServiceImpl(serviceImpl);
        templateConfig.setMapper(mapper);
        templateConfig.setXml(xml);
        templateConfig.setController(controller);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setSuperEntityClass("org.wyf.common.entity.BaseEntity");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        // 公共父类
        strategy.setSuperControllerClass("org.wyf.common.controller.BaseController");
        // 写于父类中的公共字段
        strategy.setSuperEntityColumns("isDel","createTime","createUser","modifyTime","modifyUser");
        strategy.setInclude(TABLES);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(TABLE_PREFIX);
        mpg.setStrategy(strategy);
        mpg.execute();

    }


}
