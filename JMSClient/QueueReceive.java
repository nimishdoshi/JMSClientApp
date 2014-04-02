// Author: Nimish Doshi

package examples.jms.queue;

import java.util.Hashtable;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import examples.work.UnitOfWork;
import examples.work.SimpleMatrix;
import java.io.*;

/**
 * This class is used to receive and remove messages
 * from the queue and perform Unit Of Work with the doWork() method.
 *
 */
public class QueueReceive implements MessageListener
{
  // Defines the JNDI context factory.
  public final static String JNDI_FACTORY="weblogic.jndi.WLInitialContextFactory";

  // Defines the JMS connection factory for the queue.
  public static String JMS_FACTORY="weblogic.examples.jms.QueueConnectionFactory";

  // Defines the queue.
  public final static String QUEUE="Worker.Request";
  // public final static String QUEUE="weblogic.examples.jms.exampleQueue";

  private QueueConnectionFactory qconFactory;
  private QueueConnection qcon;
  private QueueSession qsession;
  private QueueReceiver qreceiver;
  private Queue queue;
  private boolean quit = false;
  static private InitialContext ic;

  public void sendToReplyQueue(ObjectMessage om, UnitOfWork unit)
  {
	try {
		boolean isBytesMessage = false; // return Byte Message instead of Object Message
		ObjectMessage messageReturn = null;
		BytesMessage messageBytesReturn = null;

		QueueSession qsessionReturn = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

		Queue responseQueue = (Queue)om.getJMSReplyTo();
		// Got the message from Workshop/WLI instead of a Servlet
		if (responseQueue == null) {
			responseQueue = (Queue) ic.lookup("Worker.Response");
			isBytesMessage = true;
			messageBytesReturn = qsessionReturn.createBytesMessage();
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
		      ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
      		os.flush();
      		os.writeObject(unit);
      		os.flush();
      		//retrieves byte array
      		byte[] sendBuf = byteStream.toByteArray();
			messageBytesReturn.writeBytes(sendBuf);
			os.close();
		}
		else {
			messageReturn = qsessionReturn.createObjectMessage();
			messageReturn.setObject(unit);
		}
		QueueSender responseQsender = qsessionReturn.createSender(responseQueue);
		if (isBytesMessage == false)
			responseQsender.send(messageReturn);
		else
			responseQsender.send(messageBytesReturn);
		responseQsender.close();
		qsessionReturn.close();
	} catch (Exception e) {
		System.out.println("sendToReplyQueue: Exception sending to reply Queue.");
		e.printStackTrace();
	}
  }

 /**
  * Message listener interface.
  * @param msg  message
  */
  public void onMessage(Message msg)
  {
    String msgText;
    try {
      System.out.println("Message Received from " + QUEUE);
      if (msg instanceof ObjectMessage) {
	  System.out.println("QueueReceive: Received Unit Of Work Message.");
      	ObjectMessage om = (ObjectMessage) msg;
		UnitOfWork unit = (UnitOfWork)om.getObject();
		unit.doWork();
		//unit.print();
		// Send Completed Message back to server on Response Queue
		sendToReplyQueue(om, unit);
       } else if (msg instanceof TextMessage){
		msgText = ((TextMessage)msg).getText();
	  	System.out.println("onMessage Received: "+ msgText );
	 	 if (msgText.equalsIgnoreCase("quit")) {
        		synchronized(this) {
          			quit = true;
          			this.notifyAll(); // Notify main thread to quit
        		}	
        	}
      }
	else {
		msgText = msg.toString();
		System.out.println("Unknown Message Type Received: "+ msgText );
	}
     
    } catch (JMSException jmse) {
      jmse.printStackTrace();
    }
  }

  /**
   * Creates all the necessary objects for receiving
   * messages from a JMS queue.
   *
   * @param   ctx	JNDI initial context
   * @param	queueName	name of queue
   * @exception NamingException if operation cannot be performed
   * @exception JMSException if JMS fails to initialize due to internal error
   */
  public void init(Context ctx, String queueName)
    throws NamingException, JMSException
  {
    qconFactory = (QueueConnectionFactory) ctx.lookup(JMS_FACTORY);
    qcon = qconFactory.createQueueConnection();
    qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
    queue = (Queue) ctx.lookup(queueName);
    qreceiver = qsession.createReceiver(queue);
    qreceiver.setMessageListener(this);
    qcon.start();
  }

  /**
   * Closes JMS objects.
   * @exception JMSException if JMS fails to close objects due to internal error
   */
  public void close()throws JMSException
  {
    qreceiver.close();
    qsession.close();
    qcon.close();
  }
/**
  * main() method.
  *
  * @param args  WebLogic Server URL
  * @exception  Exception if execution fails
  */

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.out.println("Usage: java examples.jms.queue.QueueReceive WebLogicURL ConnFactory");
      return;
    }
    JMS_FACTORY = args[1];
    ic = getInitialContext(args[0]);
    QueueReceive qr = new QueueReceive();
    qr.init(ic, QUEUE);

    System.out.println("Ready To Receive JMS Messages (To quit, send a \"quit\" message).");

    // Wait until a "quit" message has been received.
    synchronized(qr) {
      while (! qr.quit) {
        try {
          qr.wait();
        } catch (InterruptedException ie) {}
      }
    }
    qr.close();
  }

  private static InitialContext getInitialContext(String url)
    throws NamingException
  {
    Hashtable env = new Hashtable();
    env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
    env.put(Context.PROVIDER_URL, url);
    return new InitialContext(env);
  }

}




