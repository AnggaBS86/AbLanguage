package ab.cgi;


/*
2    * Copyright 2012 The Netty Project
3    *
4    * The Netty Project licenses this file to you under the Apache License,
5    * version 2.0 (the "License"); you may not use this file except in compliance
6    * with the License. You may obtain a copy of the License at:
7    *
8    *   http://www.apache.org/licenses/LICENSE-2.0
9    *
10   * Unless required by applicable law or agreed to in writing, software
11   * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
12   * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
13   * License for the specific language governing permissions and limitations
14   * under the License.
15   */
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * class untuk cgi server dari ab language
 * @author Angga BS
 */
public class ABCgiServer
{

    private final int port;
    private ExecutorService bossExecutor;
    private ExecutorService workerExecutor;

    /**
     * constructor class
     * @param port int
     */
    public ABCgiServer(int port)
    {
        this.port = port;
    }

    /**
     * jalankan cgi server
     */
    public void run()
    {

        bossExecutor = Executors.newCachedThreadPool();
        workerExecutor = Executors.newCachedThreadPool();

        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                bossExecutor,
                workerExecutor));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new ABCgiServerPipelineFactory());

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));
    }

    /**
     * void main
     * @param args String[]
     */
    public static void main(String[] args)
    {
        int port;
        port = ab.cgi.Constant.PORT_CGI_SERVER;
        new ABCgiServer(port).run();
        System.out.println("AB CGI Server berjalan pada port : " + port);
    }
}
