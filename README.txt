elasticsearch 6.3.0
ik分词器 6.3.0
Kibana 6.3.0
nginx 1.6.1
nfinx server配置：
    server {
            listen       80;
            server_name  www.leyou.com;
            #charset koi8-r;

            #access_log  logs/host.access.log  main;

            location /item {
    			#先找本地，默认根目录下的html目录
    			root   html;
    			if (!-f $request_filename) { #请求的文件不存在，就反向代理
                proxy_pass http://127.0.0.1:8084;
                break;
    			}

               # index  index.html index.htm;
            }
    		location / {
    			proxy_pass   http://127.0.0.1:9002;
    			#连接超时
    			proxy_connect_timeout 600;
    			#读取超时
    			proxy_read_timeout 600;
                #root   html;
               # index  index.html index.htm;
            }

    	}
        server {
            listen       80;
            server_name  api.leyou.com;
    		#携带包含域名的地址信息
    		proxy_set_header Host $host;

            #charset koi8-r;

            #access_log  logs/host.access.log  main;
    		location /api/upload/ {
    				proxy_pass   http://127.0.0.1:8082;
    				proxy_connect_timeout 600;
    				proxy_read_timeout 600;
    				#匹配以api开头的所有路径，把api之后的路径进行分组，(.*)代表一组组间隔"/"
    				rewrite "^/api/(.*)$" /$1 break;
            }

            location / {
    				proxy_pass   http://127.0.0.1:10010;
    				#连接超时
    				proxy_connect_timeout 600;
    				#读取超时
    				proxy_read_timeout 600;
                #root   html;
               # index  index.html index.htm;
            }
            server {
                    listen       80;
                    server_name  manager.leyou.com;

                    #charset koi8-r;

                    #access_log  logs/host.access.log  main;

                    location / {
            				proxy_pass   http://127.0.0.1:9001;
            				#连接超时
            				proxy_connect_timeout 600;
            				#读取超时
            				proxy_read_timeout 600;
                        #root   html;
                       # index  index.html index.htm;
                    }
                    error_page   500 502 503 504  /50x.html;
                    location = /50x.html {
                        root   html;
                    }
            	}

            	server {
                    listen       80;
                    server_name  image.leyou.com;

                    location / {
            				root F:/ideaWork/leyouProject;
                    }
            	}