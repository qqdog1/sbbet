![](https://github.com/qqdog1/sbbet/workflows/Java%20CI/badge.svg)
# sbbet
### 安裝及啟動

本程式可以一般方式執行  
或以docker執行


* 一般方式啟動

1. clone from git  
<pre><code>git clone https://github.com/qqdog1/sbbet.git</code></pre>  
  
2. 到對應資料夾中並package  
<pre><code>cd sbbet</pre></code>
<pre><code>mvn package</pre></code>  

3. 執行
<pre><code>java -jar ./target/sbbet-1.0.0.jar</code></pre>


* 使用docker  

1. clone from git  
<pre><code>git clone https://github.com/qqdog1/sbbet.git</code></pre>  

2. docker build


http://54.168.123.120:8080/

可改善項目  
login方式  
加密password  
error response and error msg  
docker build image  
部分行為為求開發快速DB操作2次或更多  
logger  
看能不能不return null都用exception  
unit test從入口測到尾 違背原意 應該mock下一層  
autobuild的狀態應該要可以回傳給github action?  
spring 支援的錯誤 像是沒有權限 的回傳格式要override成統一的  
email and phone 沒有檢查格式  
company name不是key所以沒檢查重複  
client name不是key所以沒檢查重複  