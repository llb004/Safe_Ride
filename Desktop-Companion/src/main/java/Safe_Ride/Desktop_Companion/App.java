package Safe_Ride.Desktop_Companion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.BasicConfigurator;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firestore.v1beta1.WriteResult;

import net.miginfocom.swing.MigLayout;

/**
 * Hello world!
 *
 */
public class App 
{
	 public static class Request {
		public String name;
     	public String phone;
     	public String location;
     	public int guests;
     	public String comment;
     	public Boolean ADA;
     	public String id;
     	public String accepted;
     	public String reason;
     	public String homeAddress;
     	
     	public Request() {
     	}
     	
     	public Request(String phone, String location, int guests, String comment, Boolean ADA, String name, String id, String reason, String accepted, String homeAddress) {
     		this.phone = phone;
     		this.location = location;
     		this.guests = guests;
     		this.comment = comment;
     		this.ADA = ADA;
     		this.name = name;
     		this.id = id;
     		this.reason = reason;
     		this.accepted = accepted;
     		this.homeAddress = homeAddress;
     	}
     }
	 
    public static void main( String[] args ) throws IOException, InterruptedException, ExecutionException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
    {
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	BasicConfigurator.configure();
        FirebaseOptions options = new FirebaseOptions.Builder()
        		.setCredentials(GoogleCredentials.getApplicationDefault())
        		.setDatabaseUrl("https://saferide-c024a.firebaseio.com/")
        		.build();
        
        FirebaseApp.initializeApp(options);
        
        final Firestore db = FirestoreClient.getFirestore();
        
		final JFrame f = new JFrame();
		f.setBackground(Color.decode("#9D2235"));
		f.setLayout(new MigLayout());
		final JPanel panel = new JPanel();
		
		panel.removeAll();
		panel.setLayout(new MigLayout("insets 0", "[grow][grow][grow][grow]"));
		panel.setBackground(Color.decode("#9D2235"));
		
		final List<Request> q = new ArrayList<Request>();
        
        //////////////////////READ-ALL/////////////////////
        ApiFuture<QuerySnapshot> query = db.collection("requests").get();
        
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for(QueryDocumentSnapshot document : documents) {
        	Request loader = null;
        	loader = document.toObject(Request.class);
        	q.add(loader);
        }
		
		JButton refresh = new JButton("Refresh");
		f.add(refresh, "dock north");
		refresh.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				panel.removeAll();
				ApiFuture<QuerySnapshot> query = db.collection("requests").get();
				q.clear();
		        
		        QuerySnapshot querySnapshot = null;
				try {
					querySnapshot = query.get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
		        for(QueryDocumentSnapshot document : documents) {
		        	
		        	Request compare = null;
		        	compare = document.toObject(Request.class);
		        	
		        	if(!(compare.reason.equals("NON")) ||  !(compare.accepted.equals("pending")))
		        	{
		        		System.out.println(compare.reason + " : " + compare.accepted);
		        		db.collection("requests").document(document.getId()).delete();
		        		System.out.println("deleted");
		        	}
		        	else
		        	{
			        	Request loader = null;
			        	loader = document.toObject(Request.class);
			        	q.add(loader);
		        	}
		        }
		        
		        for(final Request r : q) {
		        	JTextArea requestInfo = new JTextArea("ID: " + r.id + "\n" + "Name: " + r.name + "\n" + "Location: " + r.location + "\nHome Address: " + r.homeAddress +  "\n" + "Guests: " + r.guests + "\n" + "Phone: " + r.phone + "\n" + "ADA: " + r.ADA + "\n" + "Comment: " + r.comment);
		    		JScrollPane scroll = new JScrollPane(requestInfo);
		    		requestInfo.setLineWrap(true);
		    		requestInfo.setWrapStyleWord(true);
		    		requestInfo.setEditable(false);
		    		panel.add(scroll, "w 300!, h 100!, top");
		    		
		    		JButton accept = new JButton("accept");
		    		accept.addActionListener(new ActionListener()
		    				{

								public void actionPerformed(ActionEvent e) {
									DocumentReference docRef = db.collection("requests").document(r.id);
							        
							        
							        
							        docRef.update("accepted", "accepted");
								}
		    				}
		    				);
		    		panel.add(accept, "w 100!, h 100!");
		    		
		    		JButton deny = new JButton("deny");
		    		panel.add(deny, "w 100!, h 100!");
		    		final JTextField reason = new JTextField();
		    		reason.setMinimumSize(reason.getPreferredSize());
		    		panel.add(reason, "w 300!, h 100!, wrap");
		    		
		    		deny.addActionListener(new ActionListener()
					{

						public void actionPerformed(ActionEvent e) {
							DocumentReference docRef = db.collection("requests").document(r.id);
					        
					        
					        
					        docRef.update("reason", reason.getText());
						}
					}
					);
		        }
		        panel.revalidate();
		        f.revalidate();
			}
		}
		);
		
		
        for(final Request r : q) {
        	JTextArea requestInfo = new JTextArea("ID: " + r.id + "\n" + "Name: " + r.name + "\n" + "Location: " + r.location + "\nHome Address: " + r.homeAddress + "\n" + "Guests: " + r.guests + "\n" + "Phone: " + r.phone + "\n" + "ADA: " + r.ADA + "\n" + "Comment: " + r.comment);
    		JScrollPane scroll = new JScrollPane(requestInfo);
    		requestInfo.setLineWrap(true);
    		requestInfo.setWrapStyleWord(true);
    		requestInfo.setEditable(false);
    		panel.add(scroll, "w 300!, h 100!, top");
    		
    		JButton accept = new JButton("accept");
    		accept.addActionListener(new ActionListener()
    				{

						public void actionPerformed(ActionEvent e) {
							DocumentReference docRef = db.collection("requests").document(r.id);
					        
					        
					        
					        docRef.update("accepted", "accepted");
						}
    				}
    				);
    		panel.add(accept, "w 100!, h 100!");
    		
    		JButton deny = new JButton("deny");
    		panel.add(deny, "w 100!, h 100!");
    		final JTextField reason = new JTextField();
    		reason.setMinimumSize(reason.getPreferredSize());
    		panel.add(reason, "w 300!, h 100!, wrap");
    		
    		deny.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e) {
					DocumentReference docRef = db.collection("requests").document(r.id);
			        
			        
			        
			        docRef.update("reason", reason.getText());
				}
			}
			);
        }

		panel.setSize(panel.getPreferredSize());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.add(panel);
		f.pack();
		f.setVisible(true);

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        /////////////////////READ--SINGLE//////////////////////////////
//        DocumentReference docRef = db.collection("Test").document("sample_key");
//        ApiFuture<DocumentSnapshot> future = docRef.get();
//        
//        DocumentSnapshot document = future.get();
//        
//        Request request = null;
//        
//        request = document.toObject(Request.class);
//        
//        System.out.println("Phrase: " + request.name);
        
        
        
        
        
        
        
        //////////////////////WRITE//////////////////////////
//        DocumentReference docRef = db.collection("Test").document("sample_key");
//        
//        
//        
//        Map<String, Object> data = new HashMap<String, Object>();
//        data.put("phrase", "Hello World");
//        
//        ApiFuture<com.google.cloud.firestore.WriteResult> result = docRef.set(data);
//        
//        System.out.println("Update time : " + result.get());
        
    }
}
