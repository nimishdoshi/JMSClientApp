// Author: Nimish Doshi

package examples.work;

import java.util.Random;
import java.util.Date;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;


public class SimpleMatrix implements UnitOfWork

{
    static final long serialVersionUID = 2L;
    private Integer m1[][];
    private Integer m2[][];
    private Integer result[][];
    private Integer rows =  new Integer(3);
    private Integer cols =  new Integer(3);

   // May initialize m1 an m2 by locating records from a database
   // In this example, random integers are used to initialize m1 and m2
	public SimpleMatrix() {
	  System.out.println("Created another SimpleMatrix");
        Random ran = new Random(new Date().getTime());
        m1 = new Integer[rows.intValue()][cols.intValue()];
        m2 = new Integer[rows.intValue()][cols.intValue()];
        for (int i=0;i<rows.intValue(); i++) {
            for(int j=0; j<cols.intValue(); j++) {
                m1[i][j] = new Integer(Math.abs(ran.nextInt()));
                m2[i][j] = new Integer(Math.abs(ran.nextInt()));
            }
        }
	
	}

	// This method actually multiplies m1 x m2 and stores in result
	public void doWork() {
        result = new Integer[rows.intValue()][cols.intValue()];
        for(int i = 0; i < rows.intValue(); i++) {
            for(int j = 0; j < cols.intValue(); j++) {
                int dot_product = 0;
                for(int k = 0; k < rows.intValue(); k++)
                    dot_product = dot_product + (m1[i][k].intValue() * m2[k][j].intValue());
                result[i][j] = new Integer(Math.abs(dot_product));
            }
        }
	
	}

	// This method stores result into a backing store.
	public void store() {
        // not necessary to implement for demo purposes
	}

	// This method stores result into a file.
	public void storeFile(String filename) {
       		try {
 
			System.out.println("SimpleMatrix:Store: Filename is " + filename);
		

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream(filename, true)));

			Date date=new Date();
        		bw.write(date.toString() + " Contents of matrix:\n");

        		for (int i=0; i<rows.intValue(); i++) {
            			for(int j=0; j<cols.intValue(); j++) {
                			bw.write(result[i][j].intValue() + "    ");
            			}
            			bw.write("\n");
       			}
			bw.close();
  
		} catch (IOException e) {
			System.out.println("SimpleMatrix:store Error " + e.getMessage());
			e.printStackTrace();
		}
	}

	// This method prints the current contents of result
	public void print() {
        
       if (result == null) {
            System.out.println("No results available for this matrix.");
            return;
        }
        
	Date date=new Date();

        System.out.println(date.toString() + " Contents of matrix:");
        for (int i=0; i<rows.intValue(); i++) {
            for(int j=0; j<cols.intValue(); j++) {
                System.out.print(result[i][j].intValue() + "    ");
            }
            System.out.println();
       }
	
	}
}

