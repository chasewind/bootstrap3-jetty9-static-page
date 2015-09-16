/**
 * This example uses a Java spider to scan a Web site
 * and check for broken links.
 * 
 * @author Chen Lei
 * @version 1.0
 */


package com.belief.crawler.spider;

import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;


public class CheckLinks extends JFrame implements Runnable,ISpiderReportable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6373629338632696209L;

	boolean frameSizeAdjusted = false;

  
  JLabel label1 = new JLabel();
  JLabel label2=new JLabel();
  JLabel label3=new JLabel();
  JLabel label4=new JLabel();
  JLabel label5=new JLabel();
  JLabel lblStartTime=new JLabel();
  JLabel lblProcessTime=new JLabel();
  JLabel lblProcessSpeed=new JLabel();
  

  /**
   * The begin or cancel button
   */
  JButton begin = new JButton();
  
  /**
   * The URL being processed
   */
   JTextField url = new JTextField("http://www.baidu.com/");
   /**************************************************************/
	Choice chcDepth = new Choice();// 用于在界面上设定搜索深度
	JTextField txtFileType = new JTextField("text/html");// 设定搜索的文件类型
	Choice chcFileSize = new Choice(); // 设定搜索文件的大小
   /***************************************************************/
  /**
   * Scroll the errors.
   */
   
	JScrollPane errorScroll = new JScrollPane();// errorUrl输出的面板设置
	JScrollPane inUrlScroll = new JScrollPane();// inUrl输出的面板设置
	JScrollPane ioutUrlScroll = new JScrollPane();// outUrl输出的面板设置

  /**
   * A place to store the errors created
   */
	JTextArea inURL = new JTextArea(); // inUrl输出的文本域
	JTextArea outURL = new JTextArea(); // outUrl输出的文本域
	JTextArea errors = new JTextArea(); // errorUrl输出的文本域
  JLabel current = new JLabel();
	JLabel inUrlLinksLabel = new JLabel(); // 用于inUrl数量的标签
	JLabel outUrlLinksLabel = new JLabel(); // 用于outUrl数量的标签
	JLabel goodLinksLabel = new JLabel(); // 用于goodLinksUrl数量的标签
	JLabel badLinksLabel = new JLabel(); // 用于badLinksUrl数量的标签
  
  
  /**
   * The str is the URL's title
   */
  protected String str;
  
  /**
   * The background spider thread
   */
  protected Thread backgroundThread;

  /**
   * The spider object being used
   */
  protected Spider spider;

  /**
   * The URL that the spider began with
   */
  protected URL base;

  /**
   * How many bad links have been found
   */
   
	protected int intWebDepth = 0; // 搜索深度
	protected int intWebSize = 0; // 文件大小
	protected String strWebType; // 文件类型
	protected int intInUrl = 0; // inUrl的数量
	protected int intOutUrl = 0; // outUrl的数量
	protected int badLinksCount = 0; // badLinksUrl的数量
	protected int goodLinksCount = 0; // goodLinksUrl的数量
	protected int intStartTime; // 记录开始时间
	protected int intNowTime; // 抓取过程的时间
	protected int intProcessTime; // 处理时间
  protected float fltN=100f;     
	protected String strStartTime; // 记录开始时间
  
  
  /**
   * How many good links have been found
   */
  
  
	protected Collection<Object> ErrorUrl = new ArrayList<Object>(3); // 用来记录errorUrl的集合
	protected Collection<Object> InUrl = new ArrayList<Object>(3); // 用来记录inUrl的集合
	protected Collection<Object> OutUrl = new ArrayList<Object>(3); // 用来记录outUrl的集合
	protected Collection<Object> UrlCollection = new ArrayList<Object>(3); // 用来记录已处理过Url的集合

  /**
   * The constructor. Perform setup here.
   */
  public CheckLinks()
  {
   
		setTitle("                网络蜘蛛界面");
    getContentPane().setLayout(null);
    setSize(405,650);
    setVisible(false);
		label1.setText("输入地址:");
    getContentPane().add(label1);
    label1.setBounds(12,12,84,12);
    getContentPane().add(label2);
    label2.setBounds(108,12,288,12);
		begin.setText("开始");
		begin.setActionCommand("开始");
    getContentPane().add(begin);
    begin.setBounds(12,36,84,24);
    getContentPane().add(url);
    url.setBounds(108,36,288,24);
    /*****************************************/
		label3.setText("深度"); //
    getContentPane().add(label3);            //
    label3.setBounds(12,65,35,20);           //
		chcDepth.add("  5  "); // 在图形界面增加
		chcDepth.add("  10 "); // 进行深度设置的
		chcDepth.add("  15 "); // 复选框
    chcDepth.add("  20 ");                   //
    chcDepth.add("  25 ");                   //
    chcDepth.add("  30 ");                      //
    getContentPane().add(chcDepth);          // 
    chcDepth.setBounds(53,65,60,20);         //
    /*****************************************/
		label4.setText("文件类型"); //
		getContentPane().add(label4); // 图形界面增加
		label4.setBounds(123, 65, 70, 20); // 用于文件类型
		getContentPane().add(txtFileType); // 设置的文本框
    txtFileType.setBounds(194,65,50,20);     //
    /*****************************************/
		label5.setText("文件大小"); //
    getContentPane().add(label5);            //
    label5.setBounds(255,65,70,20);          //
		chcFileSize.add("40 K"); // 图形界面增加
		chcFileSize.add("50 K"); // 用于文件类型
		chcFileSize.add("60 K"); // 选择的复选框
    chcFileSize.add("20 K");                 //
    chcFileSize.add("30 K");                 //
    getContentPane().add(chcFileSize);       //
    chcFileSize.setBounds(326,65,55,20);     //
    /***************************************************/
		lblStartTime.setText("开始时间:" + strStartTime); //
		getContentPane().add(lblStartTime); // 显示开始处理的时间
    lblStartTime.setBounds(12,87,330,12);              //
    /***************************************************/
		current.setText("正在处理: ");
    getContentPane().add(current);
    current.setBounds(12,105,384,12);
		badLinksLabel.setText("死链接: 0");
    getContentPane().add(badLinksLabel);
    badLinksLabel.setBounds(12,123,192,12);
		goodLinksLabel.setText("正常链接: 0");
    getContentPane().add(goodLinksLabel);
    goodLinksLabel.setBounds(220,123,192,12);
    errorScroll.setAutoscrolls(true);
    errorScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    errorScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    errorScroll.setOpaque(true);
    getContentPane().add(errorScroll);
    errorScroll.setBounds(12,140,384,146);
    errors.setEditable(false);
    errorScroll.getViewport().add(errors);
    errors.setBounds(0,0,366,138);
    /**********************************************/
		lblProcessTime.setText("处理时间:0"); //
		getContentPane().add(lblProcessTime); // 显示处理时间
    lblProcessTime.setBounds(12,292,150,12);      //
		lblProcessSpeed.setText("处理速度:0"); //
		getContentPane().add(lblProcessSpeed); // 显示处理速度
    lblProcessSpeed.setBounds(190,292,150,12);    //
    /**********************************************/
		// 在图形界面设置用于输出站内URL的文本域
		inUrlLinksLabel.setText("站内URL链接: 0");
    getContentPane().add(inUrlLinksLabel);
    inUrlLinksLabel.setBounds(12,309,192,12);
    inUrlScroll.setAutoscrolls(true);
    inUrlScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    inUrlScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    inUrlScroll.setOpaque(true);
    getContentPane().add(inUrlScroll);
    inUrlScroll.setBounds(12,325,384,146);
    inURL.setEditable(false);
    inUrlScroll.getViewport().add(inURL);
    inURL.setBounds(0,0,366,138);
    /**********************************************/
		// 在图形界面设置用于输出站外URL的文本域
		outUrlLinksLabel.setText("站外URL链接: 0");
    getContentPane().add(outUrlLinksLabel);
    outUrlLinksLabel.setBounds(12,476,192,12);
    ioutUrlScroll.setAutoscrolls(true);
    ioutUrlScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    ioutUrlScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    ioutUrlScroll.setOpaque(true);
    getContentPane().add(ioutUrlScroll);
    ioutUrlScroll.setBounds(12,492,384,146);
    outURL.setEditable(false);
    ioutUrlScroll.getViewport().add(outURL);
    outURL.setBounds(0,0,366,138);
    
    
    SymAction lSymAction = new SymAction();
    begin.addActionListener(lSymAction);
    
  }

  /**
   * Main method for the application
   * 
   * @param args Not used
   */
  static public void main(String args[])
  {
    (new CheckLinks()).setVisible(true);
    
  }

  /**
   * Add notifications.
   */
  public void addNotify()
  {
    // Record the size of the window prior to calling parent's
    // addNotify.
    Dimension size = getSize();

    super.addNotify();

    if ( frameSizeAdjusted )
      return;
    frameSizeAdjusted = true;

    // Adjust size of frame according to the insets and menu bar
    Insets insets = getInsets();
    javax.swing.JMenuBar menuBar = getRootPane().getJMenuBar();
    int menuBarHeight = 0;
    if ( menuBar != null )
      menuBarHeight = menuBar.getPreferredSize().height;
      setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height + menuBarHeight);
  }

  // Used by addNotify

  
  /**
   * Internal class used to dispatch events
   * 
   * @author Chenlei
   * @version 1.0
   */
  class SymAction implements java.awt.event.ActionListener {
    public void actionPerformed(java.awt.event.ActionEvent event)
    {
      Object object = event.getSource();
      if ( object == begin ){
				// 获得开始抓取的时间
        java.util.Date now = new java.util.Date();
				SimpleDateFormat dateformat = new SimpleDateFormat(" yyyy年MM月dd日 HH:mm:ss");
        strStartTime=dateformat.format(now); 
				// 获得开始抓取的时刻
        Calendar c = Calendar.getInstance();
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int minute=c.get(Calendar.MINUTE);
        int second=c.get(Calendar.SECOND);
        intStartTime=hour*3600+minute*60+second;
        //intStartTime=(new Date()).getHours()*3600+(new Date()).getMinutes()*60+(new Date()).getSeconds();
        begin_actionPerformed(event);
				// 获得设定的搜索深度
        intWebDepth=chcDepth.getSelectedIndex();
				// 获得搜索的文件大小
        intWebSize=chcFileSize.getSelectedIndex();
				// 获得搜索的文件类型
        strWebType=txtFileType.getText();
        }
    }
  }

  /**
   * Called when the begin or cancel buttons are clicked
   * 
   * @param event The event associated with the button.
   */
  void begin_actionPerformed(java.awt.event.ActionEvent event)
  {
    if ( backgroundThread==null ) {
			begin.setText("停止");
      backgroundThread = new Thread(this);
      backgroundThread.start();
      goodLinksCount=0;
      badLinksCount=0;
    } else {
      spider.cancel();
    }

  }

  /**
   * Perform the background thread operation. This method
   * actually starts the background thread.
   */
  public void run()
  {
    try {
      errors.setText("");
      spider = new Spider(this);
      spider.clear();
      base = new URL(url.getText());
      spider.addURL(base);
      spider.begin();
      Runnable doLater = new Runnable()
      {
        public void run()
        {
					begin.setText("开始");
        }
      };
      SwingUtilities.invokeLater(doLater);
      backgroundThread=null;

    } catch ( MalformedURLException e ) {
      UpdateErrors err = new UpdateErrors();
			err.msg = "无效地址！";
      SwingUtilities.invokeLater(err);

    }
  }

  /**
   * Called by the spider when a URL is found. It is here
   * that links are validated.
   * 
   * @param base The page that the link was found on.
   * @param url The actual link address.
   */
  public boolean spiderFoundURL(URL base,URL url)
  {
		// 获得处理时的时刻
  	Calendar c = Calendar.getInstance();
  	int hour = c.get(Calendar.HOUR_OF_DAY);
  	int minute = c.get(Calendar.MINUTE);
  	int second = c.get(Calendar.SECOND);
  	intNowTime = hour*3600+minute*60+second;
  	//intNowTime=(new Date()).getHours()*3600+(new Date()).getMinutes()*60+(new Date()).getSeconds();
		// 计算处理时间
  	intProcessTime=intNowTime-intStartTime;
    UpdateCurrentStats cs = new UpdateCurrentStats();
    cs.msg = url.toString();
    SwingUtilities.invokeLater(cs);
   
    if(UrlCollection.contains(url)) return false;
    else UrlCollection.add(url);
    if ( !checkLink(url) ) {
      UpdateErrors err = new UpdateErrors();
      err.msg = url+"(on page " + base + ")\n";
      SwingUtilities.invokeLater(err);
			// 统计错误URL的数量
      badLinksCount++;
			// 将错误URL增加到ErrorUrl列表
      ErrorUrl.add(url);
			// 将ErrorUrl列表写入到外部文件
      SaveUrlToFile("Errors.txt",ErrorUrl);
			// 获取错误列表中URl的title
      GetTitle(ErrorUrl);
			// 存入数据库
      SaveUrlToDateBase(ErrorUrl,"errorUrl",str);
      return false;
    }

    goodLinksCount++;
    if ( !url.getHost().equalsIgnoreCase(base.getHost()) )
    { 
      
      UpdateOutUrl outUrl = new UpdateOutUrl();
      outUrl.msg = url.toString()+"\n";
      SwingUtilities.invokeLater(outUrl);
			// 统计站外URL的数量
      intOutUrl++;
			// 将站外URL增加到OutUrl列表
      OutUrl.add(url);
			// 将OutUrl列表写入到外部文件
      SaveUrlToFile("OutUrls.txt",OutUrl);
			// 获取站外列表中URl的title
      GetTitle(OutUrl);
			// 将站外URL存入数据库
      SaveUrlToDateBase(OutUrl,"outUrl",str);
      return false;
      }
    else
    { 
      
      UpdateInUrl inUrl = new UpdateInUrl();
      inUrl.msg = url.toString()+"\n";
      SwingUtilities.invokeLater(inUrl);
			// 统计站内URL的数量
      intInUrl++;
			// 将站内URL增加到InUrl列表
      InUrl.add(url);
			// 将InUrl列表写入到外部文件
      SaveUrlToFile("InUrls.txt",InUrl);
			// 获取站内列表中URl的title
      GetTitle(InUrl);
			// 存入数据库
      SaveUrlToDateBase(InUrl,"inUrl",str);          
          
      return true;
      }
  }
  
  
  
	  
	  
  

  /**
   * Called when a URL error is found
   * 
   * @param url The URL that resulted in an error.
   */
  public void spiderURLError(URL url)
  {
  }

  /**
   * Called internally to check whether a link is good
   * 
   * @param url The link that is being checked.
   * @return True if the link was good, false otherwise.
   */
  protected boolean checkLink(URL url)
  {
    try {
      URLConnection connection = url.openConnection();
      connection.connect();
      return true;
    } catch ( IOException e ) {
      return false;
    }
  }

  /**
   * Called when the spider finds an e-mail address
   * 
   * @param email The email address the spider found.
   */
  public void spiderFoundEMail(String email)
  {
  }
  
	// 实现在ISpiderReportable中定义的方法,用于获得搜索深度
  public int getWebDepth()
  { 
    int webdepth=30;
  	switch(intWebDepth){
  	case 0:webdepth=5;break;
  	case 1:webdepth=10;break;
  	case 2:webdepth=15;break;
  	case 3:webdepth=20;break;
  	case 4:webdepth=25;break;
  	case 5:webdepth=30;break;
  	}
  	return webdepth;
  	}
  	
  	
	// 实现在ISpiderReportable中定义的方法,用于获得文件类型
  	public String getWebType(){
  		return strWebType;
  	}
  	
  	
	// 实现在ISpiderReportable中定义的方法,用于获得搜索文件大小
  	public int getWebSize(){
    int websize=30;
  	switch(intWebSize){
  	case 0:websize=40;break;
  	case 1:websize=50;break;
  	case 2:websize=60;break;
  	case 3:websize=20;break;
  	case 4:websize=30;break;	
  	}
  	return websize;
  	}
  
  	
  /**
   * Called when need to get the URL's title 
   * 
   * @param Url
   * @return str
   */	
  public String GetTitle(Collection Url)
  {
	  Object list[] =new Object[5000];
	 
	  try
	  {
		  if ( !Url.isEmpty() ) 
			  list= Url.toArray();      
		  for ( int j=0;j<list.length;j++ )
		  {
			  URL url=(URL)list[j];
			  BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		      
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
		    			  str+=c;
		    			  c=s.substring(i,++i);
		    			  
		    		  }
		    	  }
		    	  //sb.append(s + "\r\n");
		          
		          
		      }
		      //System.out.println(sb.toString());
			  return str;
		  }
		  
	  }	  
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	return null;
	  
  }
  	
  
	// 该方法用于将errorUrl列表、OutUrl列表和InUrl列表写入strFileName文件中
 public void SaveUrlToFile(String strFileName,Collection Url)
  {  
    int n=0;
    Object list[]=new Object[5000];
  	try{
			// 初始化写入实例
  		PrintWriter writeFile=new PrintWriter(new FileOutputStream(strFileName));
  		writeFile.write("******************************START TO CATCH***************************");
  		writeFile.println(' ');
  	    if ( !Url.isEmpty() ) 
          list= Url.toArray();      
        for ( int i=0;i<list.length;i++ ){
        	n=list.length;
        	java.util.Date now=new java.util.Date();
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        	String strMsg=(dateformat.format(now))+"  "+((URL)list[i]).toString();
        	writeFile.write(strMsg);
        	writeFile.println(' ');
  	  
  	    }
  	  writeFile.println(' ');
  	  writeFile.write("Have catch "+n+" Reconds");
  	  writeFile.println(' ');
  	  writeFile.write("********************************END CATCH******************************");
  	  writeFile.flush();
  	  writeFile.close();
  	}catch(IOException err)
  	{
  		err.printStackTrace();
  	}
  	}
  	
  	
/**
 * called when the processed URL is ready for 
 * storing to the database
 * 
 * @param Url The Url represents for three lists 
 * @param status The status represents the state of URL
 */
	// 该方法用于将三个列表的URL写入数据库中
public void SaveUrlToDateBase(Collection Url,String status,String title){
	String st=status;
	String sqlstmt="";	
	Statement sql=null;	
	//ResultSet rs=null;
	Object list[] = new Object[5000];
	//Spider s=new Spider(null);
	//String str=s.GetTitle();
	//System.out.println(str);
	
	
	java.util.Date now=new java.util.Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
	String date=dateformat.format(now);
	
	try{
			         	
      	Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
        String dbUrl = "jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=spider";
        Connection conn = DriverManager.getConnection(dbUrl,"sa","chenlei");
        
        
        
        if ( !Url.isEmpty() )
        	list= Url.toArray();      
        for ( int i=0;i<list.length;i++ ){
        	sql = conn.createStatement();
            //ResultSet rs = sql.executeQuery("select * from url");
            //while(rs.next())   
            //{   
        	        //System.out.print(rs.getString("url") + "");
                    //System.out.print(rs.getString("status")   +   "         ");   
                    //System.out.print(rs.getString("date")   +   "         ");   
                    
            //}
        	        	
        	URL url=((URL)list[i]);          
            
        	String ulist=url.toString();
        	sqlstmt="insert into webspider values('"+ulist+"','"+st+"','"+date+"','"+title+"')";
            sql.executeUpdate(sqlstmt);
        	
        }
        	
        //rs.close();
        sql.close();
        conn.close();
      	
    }catch(Exception ex){
      	System.err.println(ex.getMessage());
		
	}
	
}


  

  /**
   * 
   * Internal class used to update the error information
   * in a Thread-Safe way
   * 
   * @author Chenlei
   *
   */
  class UpdateErrors implements Runnable {
    public String msg;
    java.util.Date now=new java.util.Date();
		SimpleDateFormat dateformat = new SimpleDateFormat(" yyyy年MM月dd日 HH:mm:ss");
    public void run()
    {
      errors.append((dateformat.format(now))+" "+msg);
    }
  }
  
  class UpdateInUrl implements Runnable {
    public String msg;
    java.util.Date now=new java.util.Date();
		SimpleDateFormat dateformat = new SimpleDateFormat(" yyyy年MM月dd日 HH:mm:ss");
    public void run()
    {
      inURL.append((dateformat.format(now))+" "+msg);
    }
  }
  
  class UpdateOutUrl implements Runnable {
    public String msg;
    java.util.Date now=new java.util.Date();
		SimpleDateFormat dateformat = new SimpleDateFormat(" yyyy年MM月dd日 HH:mm:ss");
    public void run()
    {
      outURL.append((dateformat.format(now))+" "+msg);
    }
  }
  /**
   * Used to update the current status information
   * in a "Thread-Safe" way
   * 
   * @author Chenlei
   */

  class UpdateCurrentStats implements Runnable {
    public String msg;
    public void run()
    {
			current.setText("正在处理: " + msg);
			goodLinksLabel.setText("正常链接: " + goodLinksCount);
			badLinksLabel.setText("死链接: " + badLinksCount);
			lblStartTime.setText("开始时间:" + strStartTime);
			lblProcessTime.setText("处理时间:" + intProcessTime + "s");
      try{
    	     	  
    	  float speed=((goodLinksCount*100)/intProcessTime)/fltN;
				lblProcessSpeed.setText("处理速度:" + speed + "页/S");
      }
      catch(ArithmeticException e){
    	  e.printStackTrace();    	  
      }
      
			inUrlLinksLabel.setText("站内URL链接:" + intInUrl);
			outUrlLinksLabel.setText("站外URL链接:" + intOutUrl);
    }
  }
}


