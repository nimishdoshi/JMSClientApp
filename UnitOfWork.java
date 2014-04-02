// Author: Nimish Doshi

package examples.work;

import java.io.Serializable;

public interface UnitOfWork extends java.io.Serializable
{
    // This method executes itself on the client machine
	public void doWork();

    // This method prints the current contents of performed work
	public void print();
	
    
    // This method stores the instance into a backing store.
	public void store();


    // This method stores the instance into a file.
    	public void storeFile(String filename);
	

}

