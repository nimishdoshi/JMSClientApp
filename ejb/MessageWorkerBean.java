// Author: Nimish Doshi

package examples.work;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import examples.work.UnitOfWork;
import examples.work.SimpleMatrix;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Nimish Doshi
 */
public class MessageWorkerBean implements MessageDrivenBean, MessageListener {

  private static final boolean VERBOSE = true;
  private MessageDrivenContext m_context;
  
  // You might also consider using WebLogic's log service
  private void log(String s) {
    if (VERBOSE) System.out.println(s);
  } 
  
  /**
   * This method is required by the EJB Specification,
   * but is not used by this example.
   *
   */
  public void ejbActivate() {
    log("ejbActivate called");
  }

  /**
   * This method is required by the EJB Specification,
   * but is not used by this example.
   *
   */
  public void ejbRemove() {
    log("ejbRemove called");
  }

  /**
   * This method is required by the EJB Specification,
   * but is not used by this example.
   *
   */
  public void ejbPassivate() {
    log("ejbPassivate called");
  }

  /**
   * Sets the session context.
   *
   * @param ctx               MessageDrivenContext Context for session
   */
  public void setMessageDrivenContext(MessageDrivenContext ctx) {
    m_context = ctx;
  }

  /**
   *
   * @exception               javax.ejb.CreateException if there is
   *                          a communications or systems failure
   */
  public void ejbCreate () throws CreateException {

  }

  /////
  // Implementation of MessageListener
  //

  /**
   * Retrieve the int value of the TextMessage and increment the RMI counter by
   * that much.
   *
   */
  public void onMessage(Message msg) {
    ObjectMessage om = (ObjectMessage) msg;
    String filename;
    try {
      UnitOfWork unit = (UnitOfWork)om.getObject();
      log("Message Driven Bean: Received new Unit of Work.");
      filename=(String)m_context.lookup("java:comp/env/filename");
      //System.out.println("Filename is: " + filename);
      unit.print();
      unit.storeFile(filename);
    }
    catch(JMSException ex) {
	log("Messsage Driven Bean: Could not retrieve Unit Of Work.");
      ex.printStackTrace();
    }
  }

  //
  // Implementation of MessageListener
  /////

  static void p(String s) {
    System.out.println("*** <MessageWorkerBean> " + s);
  }
}


