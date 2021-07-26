/*    */ package com.demo.index;
/*    */ 
/*    */ import com.jfinal.aop.Interceptor;
/*    */ import com.jfinal.aop.Invocation;
/*    */ import com.jfinal.core.Controller;
/*    */ import javax.servlet.http.HttpSession;
/*    */ 
/*    */ public class AdminInterceptor
/*    */   implements Interceptor
/*    */ {
/*    */   public void intercept(Invocation inv)
/*    */   {
/* 17 */     HttpSession session = inv.getController().getSession();
/* 18 */     if (session == null) {
/* 19 */       inv.getController().redirect("/admin/login");
/*    */     } else {
/* 21 */       String nickname = (String)session.getAttribute("nickname");
/* 22 */       if (nickname != null)
/* 23 */         inv.invoke();
/*    */       else
/* 25 */         inv.getController().redirect("/admin/login");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zjm\git\ocr\face_demo\WebRoot\WEB-INF\classes\
 * Qualified Name:     com.demo.index.AdminInterceptor
 * JD-Core Version:    0.6.0
 */