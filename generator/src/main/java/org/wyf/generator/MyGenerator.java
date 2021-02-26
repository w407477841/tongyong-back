//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.wyf.generator;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class MyGenerator {
    private static final Logger logger = LoggerFactory.getLogger(MyGenerator.class);
    protected ConfigBuilder config;
    protected InjectionConfig injectionConfig;
    private DataSourceConfig dataSource;
    private StrategyConfig strategy;
    private PackageConfig packageInfo;
    private TemplateConfig template;
    private GlobalConfig globalConfig;
    private VelocityEngine engine;

    public MyGenerator() {
    }

    public void execute() {
        logger.debug("==========================准备生成文件...==========================");
        this.initConfig();
        this.mkdirs(this.config.getPathInfo());
        Map<String, VelocityContext> ctxData = this.analyzeData(this.config);
        Iterator i$ = ctxData.entrySet().iterator();

        while(i$.hasNext()) {
            Entry<String, VelocityContext> ctx = (Entry)i$.next();
            this.batchOutput((String)ctx.getKey(), (VelocityContext)ctx.getValue());
        }

        if (this.config.getGlobalConfig().isOpen()) {
            try {
                String osName = System.getProperty("os.name");
                if (osName != null) {
                    if (osName.contains("Mac")) {
                        Runtime.getRuntime().exec("open " + this.config.getGlobalConfig().getOutputDir());
                    } else if (osName.contains("Windows")) {
                        Runtime.getRuntime().exec("cmd /c start " + this.config.getGlobalConfig().getOutputDir());
                    } else {
                        logger.debug("文件输出目录:" + this.config.getGlobalConfig().getOutputDir());
                    }
                }
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

        logger.debug("==========================文件生成完成！！！==========================");
    }

    protected List<TableInfo> getAllTableInfoList(ConfigBuilder config) {
        return config.getTableInfoList();
    }

    private Map<String, VelocityContext> analyzeData(ConfigBuilder config) {
        List<TableInfo> tableList = this.getAllTableInfoList(config);
        Map<String, String> packageInfo = config.getPackageInfo();
        Map<String, VelocityContext> ctxData = new HashMap();
        String superEntityClass = this.getSuperClassName(config.getSuperEntityClass());
        String superMapperClass = this.getSuperClassName(config.getSuperMapperClass());
        String superServiceClass = this.getSuperClassName(config.getSuperServiceClass());
        String superServiceImplClass = this.getSuperClassName(config.getSuperServiceImplClass());
        String superControllerClass = this.getSuperClassName(config.getSuperControllerClass());
        String date = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Iterator i$ = tableList.iterator();

        while(i$.hasNext()) {
            TableInfo tableInfo = (TableInfo)i$.next();
            VelocityContext ctx = new VelocityContext();
            if (null != this.injectionConfig) {
                this.injectionConfig.initMap();
                ctx.put("cfg", this.injectionConfig.getMap());
            }

            if (config.getGlobalConfig().isActiveRecord()) {
                tableInfo.setImportPackages("com.baomidou.mybatisplus.activerecord.Model");
            }

            if (tableInfo.isConvert()) {
                tableInfo.setImportPackages("com.baomidou.mybatisplus.annotations.TableName");
            }

            if (tableInfo.isLogicDelete(config.getStrategyConfig().getLogicDeleteFieldName())) {
                tableInfo.setImportPackages("com.baomidou.mybatisplus.annotations.TableLogic");
            }

            if (StringUtils.isNotEmpty(config.getStrategyConfig().getVersionFieldName())) {
                tableInfo.setImportPackages("com.baomidou.mybatisplus.annotations.Version");
            }

            if (StringUtils.isNotEmpty(config.getSuperEntityClass())) {
                tableInfo.setImportPackages(config.getSuperEntityClass());
            } else {
                tableInfo.setImportPackages("java.io.Serializable");
            }

            if (config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix()) {
                Iterator i$1 = tableInfo.getFields().iterator();

                while(i$1.hasNext()) {
                    TableField field = (TableField)i$1.next();
                    if (field.getPropertyType().equalsIgnoreCase("boolean") && field.getPropertyName().startsWith("is")) {
                        field.setPropertyName(config.getStrategyConfig(), StringUtils.removePrefixAfterPrefixToLower(field.getPropertyName(), 2));
                    }
                }
            }

            if (config.getStrategyConfig().isControllerMappingHyphenStyle()) {
                ctx.put("controllerMappingHyphenStyle", config.getStrategyConfig().isControllerMappingHyphenStyle());
                ctx.put("controllerMappingHyphen", StringUtils.camelToHyphen(tableInfo.getEntityPath()));
            }
            // 过滤掉父类的属性
            String [] superEntityColumns = config.getStrategyConfig().getSuperEntityColumns();
            List<TableField> fields = new ArrayList<>();
            List<TableField> commonFields = new ArrayList<>();
            tableInfo.getFields().forEach(item->{
                boolean exist = false;
                for (String superEntityColumn : superEntityColumns) {
                    if(superEntityColumn.equals(item.getPropertyName())){
                        exist  = true;
                        break;
                    }
                }
                if(!exist){
                    fields.add(item);
                }else{
                    commonFields.add(item);
                }
            });
            tableInfo.setFields(fields);
            tableInfo.setCommonFields(commonFields);


            ctx.put("restControllerStyle", config.getStrategyConfig().isRestControllerStyle());
            ctx.put("package", packageInfo);
            ctx.put("author", config.getGlobalConfig().getAuthor());
            ctx.put("logicDeleteFieldName", config.getStrategyConfig().getLogicDeleteFieldName());
            ctx.put("versionFieldName", config.getStrategyConfig().getVersionFieldName());
            ctx.put("activeRecord", config.getGlobalConfig().isActiveRecord());
            ctx.put("kotlin", config.getGlobalConfig().isKotlin());
            ctx.put("date", date);
            ctx.put("table", tableInfo);
            ctx.put("enableCache", config.getGlobalConfig().isEnableCache());
            ctx.put("baseResultMap", config.getGlobalConfig().isBaseResultMap());
            ctx.put("baseColumnList", config.getGlobalConfig().isBaseColumnList());
            ctx.put("entity", tableInfo.getEntityName());
            ctx.put("entityLowerCase", tableInfo.getEntityName().toLowerCase());
            ctx.put("entityColumnConstant", config.getStrategyConfig().isEntityColumnConstant());
            ctx.put("entityBuilderModel", config.getStrategyConfig().isEntityBuilderModel());
            ctx.put("entityLombokModel", config.getStrategyConfig().isEntityLombokModel());
            ctx.put("entityBooleanColumnRemoveIsPrefix", config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix());
            ctx.put("superEntityClass", superEntityClass);
           // ctx.put("superEntityColumns",config.getStrategyConfig().getSuperEntityColumns());
            ctx.put("superMapperClassPackage", config.getSuperMapperClass());
            ctx.put("superMapperClass", superMapperClass);
            ctx.put("superServiceClassPackage", config.getSuperServiceClass());
            ctx.put("superServiceClass", superServiceClass);
            ctx.put("superServiceImplClassPackage", config.getSuperServiceImplClass());
            ctx.put("superServiceImplClass", superServiceImplClass);
            ctx.put("superControllerClassPackage", config.getSuperControllerClass());
            ctx.put("superControllerClass", superControllerClass);
            ctxData.put(tableInfo.getEntityName(), ctx);
        }

        return ctxData;
    }

    private String getSuperClassName(String classPath) {
        return StringUtils.isEmpty(classPath) ? null : classPath.substring(classPath.lastIndexOf(".") + 1);
    }

    private void mkdirs(Map<String, String> pathInfo) {
        Iterator i$ = pathInfo.entrySet().iterator();

        while(i$.hasNext()) {
            Entry<String, String> entry = (Entry)i$.next();
            File dir = new File((String)entry.getValue());
            if (!dir.exists()) {
                boolean result = dir.mkdirs();
                if (result) {
                    logger.debug("创建目录： [" + (String)entry.getValue() + "]");
                }
            }
        }

    }

    private void batchOutput(String entityName, VelocityContext context) {
        try {
            TableInfo tableInfo = (TableInfo)context.get("table");
            Map<String, String> pathInfo = this.config.getPathInfo();
            String entityFile = String.format((String)pathInfo.get("entity_path") + File.separator + "%s" + this.suffixJavaOrKt(), entityName);
            String mapperFile = String.format((String)pathInfo.get("mapper_path") + File.separator + tableInfo.getMapperName() + this.suffixJavaOrKt(), entityName);
            String xmlFile = String.format((String)pathInfo.get("xml_path") + File.separator + tableInfo.getXmlName() + ".xml", entityName);
            String serviceFile = String.format((String)pathInfo.get("service_path") + File.separator + tableInfo.getServiceName() + this.suffixJavaOrKt(), entityName);
            String implFile = String.format((String)pathInfo.get("service_impl_path") + File.separator + tableInfo.getServiceImplName() + this.suffixJavaOrKt(), entityName);
            String controllerFile = String.format((String)pathInfo.get("controller_path") + File.separator + tableInfo.getControllerName() + this.suffixJavaOrKt(), entityName);
            String indexVueFile =  String.format((String)pathInfo.get("controller_path") + File.separator +  "index"+entityName+".vue", entityName);
            String detailVueFile =  String.format((String)pathInfo.get("controller_path") + File.separator +  "detail"+entityName+".vue", entityName);
            String apijsFile =  String.format((String)pathInfo.get("controller_path") + File.separator +  "api"+entityName+".js", entityName);
            String routerjsFile =  String.format((String)pathInfo.get("controller_path") + File.separator +  "router"+entityName+".js", entityName);
            TemplateConfig template = this.config.getTemplate();
            if (this.isCreate(entityFile)) {
                this.vmToFile(context, template.getEntity(this.config.getGlobalConfig().isKotlin()), entityFile);
            }

            if (this.isCreate(mapperFile)) {
                this.vmToFile(context, template.getMapper(), mapperFile);
            }

            if (this.isCreate(xmlFile)) {
                this.vmToFile(context, template.getXml(), xmlFile);
            }

            if (this.isCreate(serviceFile)) {
                this.vmToFile(context, template.getService(), serviceFile);
            }

            if (this.isCreate(implFile)) {
                this.vmToFile(context, template.getServiceImpl(), implFile);
                this.vmToFile(context,"/templates/detail.vue.vm",detailVueFile);
                this.vmToFile(context,"/templates/api.js.vm",apijsFile);
                this.vmToFile(context,"/templates/index.vue.vm",indexVueFile);
                this.vmToFile(context,"/templates/router.js.vm",routerjsFile);

            }

            if (this.isCreate(controllerFile)) {
                this.vmToFile(context, template.getController(), controllerFile);
            }


            if (this.injectionConfig != null) {
                List<FileOutConfig> focList = this.injectionConfig.getFileOutConfigList();
                if (CollectionUtils.isNotEmpty(focList)) {
                    Iterator i$ = focList.iterator();

                    while(i$.hasNext()) {
                        FileOutConfig foc = (FileOutConfig)i$.next();
                        if (this.isCreate(foc.outputFile(tableInfo))) {
                            this.vmToFile(context, foc.getTemplatePath(), foc.outputFile(tableInfo));
                        }
                    }
                }
            }
        } catch (IOException var15) {
            logger.error("无法创建文件，请检查配置信息！", var15);
        }

    }

    protected String suffixJavaOrKt() {
        return this.config.getGlobalConfig().isKotlin() ? ".kt" : ".java";
    }

    private void vmToFile(VelocityContext context, String templatePath, String outputFile) throws IOException {
        if (!StringUtils.isEmpty(templatePath)) {
            VelocityEngine velocity = this.getVelocityEngine();
            Template template = velocity.getTemplate(templatePath, ConstVal.UTF8);
            File file = new File(outputFile);
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                logger.debug("创建文件所在的目录失败!");
            } else {
                FileOutputStream fos = new FileOutputStream(outputFile);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, ConstVal.UTF8));
                template.merge(context, writer);
                writer.close();
                logger.debug("模板:" + templatePath + ";  文件:" + outputFile);
            }
        }
    }

    private VelocityEngine getVelocityEngine() {
        if (this.engine == null) {
            Properties p = new Properties();
            p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            p.setProperty("file.resource.loader.path", "");
            p.setProperty("UTF-8", ConstVal.UTF8);
            p.setProperty("input.encoding", ConstVal.UTF8);
            p.setProperty("file.resource.loader.unicode", "true");
            this.engine = new VelocityEngine(p);
        }

        return this.engine;
    }

    private boolean isCreate(String filePath) {
        File file = new File(filePath);
        return !file.exists() || this.config.getGlobalConfig().isFileOverride();
    }

    protected void initConfig() {
        if (null == this.config) {
            this.config = new ConfigBuilder(this.packageInfo, this.dataSource, this.strategy, this.template, this.globalConfig);
            if (null != this.injectionConfig) {
                this.injectionConfig.setConfig(this.config);
            }
        }

    }

    public DataSourceConfig getDataSource() {
        return this.dataSource;
    }

    public MyGenerator setDataSource(DataSourceConfig dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public StrategyConfig getStrategy() {
        return this.strategy;
    }

    public MyGenerator setStrategy(StrategyConfig strategy) {
        this.strategy = strategy;
        return this;
    }

    public PackageConfig getPackageInfo() {
        return this.packageInfo;
    }

    public MyGenerator setPackageInfo(PackageConfig packageInfo) {
        this.packageInfo = packageInfo;
        return this;
    }

    public TemplateConfig getTemplate() {
        return this.template;
    }

    public MyGenerator setTemplate(TemplateConfig template) {
        this.template = template;
        return this;
    }

    public ConfigBuilder getConfig() {
        return this.config;
    }

    public MyGenerator setConfig(ConfigBuilder config) {
        this.config = config;
        return this;
    }

    public GlobalConfig getGlobalConfig() {
        return this.globalConfig;
    }

    public MyGenerator setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }

    public InjectionConfig getCfg() {
        return this.injectionConfig;
    }

    public MyGenerator setCfg(InjectionConfig injectionConfig) {
        this.injectionConfig = injectionConfig;
        return this;
    }
}
