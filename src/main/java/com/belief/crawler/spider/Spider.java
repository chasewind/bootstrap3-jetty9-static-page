package com.belief.crawler.spider;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;



/**
 * That class implements a reusable spider
 * 
 * @author Chen Lei
 * @version 1.0
 */
public class Spider {
	protected int intDepth=0;
	boolean isTitle=false;
	protected String urlTitle = new String();
	//protected String title="";
	
	/**
	 * Get the title
	 */
	//public String GetTitle()
	//{
		//System.out.println(title);
		//return title;
	//}

  /**
   * A collection of URLs that resulted in an error
   */
  protected Collection<Object> workloadError = new ArrayList<Object>(3);

  /**
   * A collection of URLs that are waiting to be processed
   */
  protected Collection<Object> workloadWaiting = new ArrayList<Object>(3);

  /**
   * A collection of URLs that were processed
   */
  protected Collection<Object> workloadProcessed = new ArrayList<Object>(3);
  
  /**
   * The class that the spider should report its URLs to
   */
  protected ISpiderReportable report;
  

  /**
   * A flag that indicates whether this process
   * should be canceled
   */
  protected boolean cancel = false;

  /**
   * The constructor
   * 
   * @param report A class that implements the ISpiderReportable
   * interface, that will receive information that the
   * spider finds.
   */
  public Spider(ISpiderReportable report)
  {
    this.report = report;
  }
  
  
  /**
   * Get the URLs that resulted in an error.
   * 
   * @return A collection of URL's.
   */
	public Collection<Object> getWorkloadError() // 包含发生错误的URL
  {
    return workloadError;
  }

  /**
   * Get the URLs that were waiting to be processed.
   * You should add one URL to this collection to
   * begin the spider.
   * 
   * @return A collection of URLs.
   */
	public Collection<Object> getWorkloadWaiting() // 包含未处理的URL列表
  {
    return workloadWaiting;
  }

  /**
   * Get the URLs that were processed by this spider.
   * 
   * @return A collection of URLs.
   */
	public Collection<Object> getWorkloadProcessed() // 已经处理过且无需再次访问的URL
  {
    return workloadProcessed;
  }    

  /**
   * Clear all of the workloads.
   */
  public void clear()
  {
    getWorkloadError().clear();
    getWorkloadWaiting().clear();
    getWorkloadProcessed().clear();
  }

  /**
   * Set a flag that will cause the begin
   * method to return before it is done.
   */
  public void cancel()
  {
    cancel = true;
  }

  /**
   * Add a URL for processing.
   * 
   * @param url
   */
  public void addURL(URL url)
  {
    if ( getWorkloadWaiting().contains(url) )
      return;
    if ( getWorkloadError().contains(url) )
      return;
    if ( getWorkloadProcessed().contains(url) )
      return;
		log("添加工作量: " + url);
    getWorkloadWaiting().add(url);
  }

  /**
   * Called internally to process a URL
   * 
   * @param url The URL to be processed.
   */
	public void processURL(URL url) // 读取并解析HTML
  { float n=10.0f;
    try {
			log("正在处理: " + url);
      // get the URL's contents
      URLConnection connection = url.openConnection();
			// 进行文件类型的判断
      if ( (connection.getContentType()!=null) &&
           !connection.getContentType().toLowerCase().startsWith(report.getWebType())) {
        getWorkloadWaiting().remove(url);
        getWorkloadProcessed().add(url);
				log("没有抓取到 " + url.toString() + " 因为内容类型是: " +
             connection.getContentType() );
        return;
      }
			// 用于文件大小的判断
      if((connection.getContentLength()>report.getWebSize()*1024))
       {
       	getWorkloadWaiting().remove(url);
        getWorkloadProcessed().add(url);
				log("没有抓取到 " + url.toString() + " 因为网页内容的大小: " +
            (connection.getContentLength()*10)/1024/n+"k");
        return ;
      }
			// 用于搜索深度的判断
      if(report.getWebDepth()<intDepth)
      {
       	getWorkloadWaiting().remove(url);
        getWorkloadProcessed().add(url);
				log("没有抓取到 " + url.toString() + " 因为网页深度比 " + report.getWebDepth() + " 大");
        return;
      }
      
      // read the URL
      InputStream is = connection.getInputStream();
      Reader r = new InputStreamReader(is);
      // parse the URL
      HTMLEditorKit.Parser parse = new HTMLParse().getParser();
      parse.parse(r,new Parser(url),true);
      
			// 输出title
      //String output=new Parser(url).getURLtille();
      //System.out.println(output);
      
            
      //get HTML source code
     /* BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
      
      String s = "";
      
      //StringBuffer sb = new StringBuffer();
      
      while ((s = br.readLine()) != null)
      {
          int i=s.indexOf("<title>");
    	  if(i!=-1)
    	  {
    		  i=i+7;
    		  String c=s.substring(i,++i);
    		  String match="<";
    		  while(!c.equals(match))
    		  {
    			  title+=c;
    			  c=s.substring(i,++i);
    			  
    		  }
    	  }
    	  
    	  //sb.append(s + "\r\n");
          
          
      }
      System.out.println(title);*/
      //System.out.println(sb.toString());
      
    } catch ( IOException e ) {
      getWorkloadWaiting().remove(url);
      getWorkloadError().add(url);
			log("错误URL: " + url);
      report.spiderURLError(url);
      return;
    }
    // mark URL as complete
    getWorkloadWaiting().remove(url);
    getWorkloadProcessed().add(url);
		log("完成处理: " + url);

  }
  
  

  /**
   * Called to start the spider
   */
	public void begin() // 一直重复遍历workloadWaiting，并处理其中的每一个页面
  {
    cancel = false;
    while ( !getWorkloadWaiting().isEmpty() && !cancel ) {
      intDepth++;
      Object list[] = getWorkloadWaiting().toArray();
      for ( int i=0;(i<list.length)&&!cancel;i++ )
				processURL((URL) list[i]); // 将需要处理的URL传给processURL方法
    }
  }

/**
 * A HTML parser callback used by this class to detect links
 * 
 * @author Chen Lei
 * @version 1.0
 */
 protected class Parser extends HTMLEditorKit.ParserCallback {
    protected URL base;
    
    
    public Parser(URL base)
    {
      this.base = base;
    }

    public void handleSimpleTag(HTML.Tag t,
                                MutableAttributeSet a,int pos)
    {
      String href = (String)a.getAttribute(HTML.Attribute.HREF);
      
      if( (href==null) && (t==HTML.Tag.FRAME) )
        href = (String)a.getAttribute(HTML.Attribute.SRC);
        
      if ( href==null )
        return;

      int i = href.indexOf('#');
      if ( i!=-1 )
        href = href.substring(0,i);

      if ( href.toLowerCase().startsWith("mailto:") ) {
        report.spiderFoundEMail(href);
        return;
      }

      handleLink(base,href);
      
      if(t==HTML.Tag.TITLE)
      {
    	  isTitle=true;
      }
      
    }

    public void handleStartTag(HTML.Tag t,
                               MutableAttributeSet a,int pos)
    {
      handleSimpleTag(t,a,pos);    // handle the same way

    }
    
   /* public void handleText(char[] data, int pos)
    {
    	if (isTitle)
    	{
    		
    		   String temptitle = new String(data);
    		   urlTitle = temptitle;
    		   //System.out.println(urlTitle);
    		}
    }*/

    protected void handleLink(URL base,String str)
    {
      try {
        URL url = new URL(base,str);
        if ( report.spiderFoundURL(base,url) )
          addURL(url);
      } catch ( MalformedURLException e ) {
				log("无效的URL: " + str);
      }
    }
  }

  /**
   * Called internally to log information
   * This basic method just writes the log
   * out to the stdout.
   * 
   * @param entry The information to be written to the log.
   */
  /*public void log(String entry)
  {
    System.out.println( (new Date()) + ":" + entry );
  }*/
  
  
	// 修改log方法,用于边将entry写入屏幕,边写进指定文件,用于记录抓取日志
  public void log(String entry)
	{   
	    Date now = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat(" yyyy年MM月dd日 HH:mm:ss");
        String StartTime=dateformat.format(now); 
        String processMsg=new String(StartTime+"  "+entry);
		String s=new String();
		System.out.println(processMsg);
		
		try{
			BufferedReader bufferstring=new BufferedReader(new StringReader(processMsg));
			PrintWriter processRecord=new PrintWriter(new FileWriter("ProcessRecord.txt",true));
			
			while((s=bufferstring.readLine())!=null){			
				processRecord.write(s);
				processRecord.println();
		    }
		    processRecord.close();
		}
		catch(EOFException e)
		{
			System.err.println("结束流");
		}
		catch(IOException e){
		}
	} 
}


