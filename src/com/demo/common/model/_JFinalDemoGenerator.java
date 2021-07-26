/*    */ package com.demo.common.model;
/*    */ 
/*    */ import com.demo.common.DemoConfig;
/*    */ import com.jfinal.kit.PathKit;
/*    */ import com.jfinal.kit.PropKit;
/*    */ import com.jfinal.plugin.activerecord.generator.Generator;
/*    */ import com.jfinal.plugin.druid.DruidPlugin;
/*    */ import javax.sql.DataSource;
/*    */ 
/*    */ public class _JFinalDemoGenerator
/*    */ {
/*    */   public static DataSource getDataSource()
/*    */   {
/* 20 */     PropKit.use("a_little_config.txt");
/* 21 */     DruidPlugin druidPlugin = DemoConfig.createDruidPlugin();
/* 22 */     druidPlugin.start();
/* 23 */     return druidPlugin.getDataSource();
/*    */   }
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 28 */     String baseModelPackageName = "com.demo.common.model.base";
/*    */ 
/* 30 */     String baseModelOutputDir = PathKit.getWebRootPath() + "/../src/com/demo/common/model/base";
/*    */ 
/* 33 */     String modelPackageName = "com.demo.common.model";
/*    */ 
/* 35 */     String modelOutputDir = baseModelOutputDir + "/..";
/*    */ 
/* 38 */     Generator generator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
/*    */ 
/* 40 */     generator.setGenerateChainSetter(false);
/*    */ 
/* 42 */     generator.addExcludedTable(new String[] { "adv" });
/*    */ 
/* 44 */     generator.setGenerateDaoInModel(true);
/*    */ 
/* 46 */     generator.setGenerateChainSetter(true);
/*    */ 
/* 48 */     generator.setGenerateDataDictionary(false);
/*    */ 
/* 51 */     generator.setRemovedTableNamePrefixes(new String[] { "t_" });
/*    */ 
/* 53 */     generator.generate();
/*    */   }
/*    */ }

/* Location:           C:\Users\zjm\git\ocr\face_demo\WebRoot\WEB-INF\classes\
 * Qualified Name:     com.demo.common.model._JFinalDemoGenerator
 * JD-Core Version:    0.6.0
 */