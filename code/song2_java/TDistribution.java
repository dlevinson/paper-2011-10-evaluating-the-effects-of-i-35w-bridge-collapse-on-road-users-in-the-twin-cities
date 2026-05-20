
import java.io.*;

public class TDistribution {
	float TrafficProducedataNode[];   //// at a network node
	float TrafficAttractedtoaNode[];    //// at a network node
	float Attraction[],oldAttraction[],newAttraction[];   ////// the resulting attraction function for each node
	double denom[];    //// its the resulting denominator in the gravity model.......denom[i] = sum(j) (Attraction[j] * f(i, j))
	public double coeff = 0.048*60;// coefficent in friction function//0.1 is commonly used for friction factor, with travel in minutes
	
	
	public TDistribution( DirectedGraph dgraph) {
		int vertices=dgraph.Vertices() ;
	}
	
	public void tripDistribution(DirectedGraph dgraph, TGeneration tgen, DijkstrasAlgo dalgo) {
		
		int edges= dgraph.Edges();
		int vertices = dgraph.Vertices();
		int centroids=dgraph.Centroids();
		TrafficProducedataNode=new float [centroids];   //// at a network centroid
		TrafficAttractedtoaNode=new float [centroids];    //// at a network centroid
		TrafficProducedataNode=tgen.TrafficProducedataNode;
		TrafficAttractedtoaNode=tgen.TrafficAttractedtoaNode;
		Attraction=new float[centroids];
		oldAttraction=new float[centroids];
		newAttraction=new float[centroids];
		denom=new double[centroids];
		
			
		///////   Trip_Distribution
			float error = 2;
			
			//System.out.println("Starting Trip Distribution");
						
			for(int i=0; i<centroids; i++)
				Attraction[i] = newAttraction[i] = TrafficAttractedtoaNode[i];
			int iterationCounter = 0;
			
			while(error> 0.5) {
				
				iterationCounter++;
				error = 0;		
				
				//// step 1:   set denom =0; and oldattraction = newattraction
				for(int i=0; i<centroids; i++) {
					denom[i] = 0;
					oldAttraction[i] = newAttraction[i];
					newAttraction[i] = 0;
					//System.out.print(oldAttraction[i] + "  ");
				}
				//System.out.println();

				//// step 2:  calculate the new Attractions using the previous oldAttraction
				for(int i=0; i<centroids; i++)
					{
						if(oldAttraction[i]>0)
						Attraction[i] = TrafficAttractedtoaNode[i]*Attraction[i]/oldAttraction[i];
			
					} 
				
				//// step 3:  calculate denom with the new Attractions
				for(int i=0; i<centroids; i++)
					for(int j=0; j<centroids; j++)
						if(i!=j && dalgo.pLabel(i+1, j+1)<dgraph.INF )
						denom[i] +=Attraction[j]*Math.exp( -coeff*dalgo.pLabel(i+1, j+1) ); 
					
				//// step 4:  calculate newAttraction using denom and Attraction
				for(int j=0; j<centroids; j++) {
					for(int i=0; i<centroids; i++)
						if( i!= j && denom[i]>0 && dalgo.pLabel(i+1, j+1)<dgraph.INF )
							newAttraction[j] += TrafficProducedataNode[i]*Math.exp( -coeff*dalgo.pLabel(i+1, j+1) )/denom[i];
					newAttraction[j] *= Attraction[j];
				}
				
				//// step 5:  calculate error. error = square root of sum of squares of deviation rom previous results
				for(int i=0; i<centroids; i++)
					error += Math.pow( (oldAttraction[i] - newAttraction[i] ),  2);					
				error = (float)Math.sqrt( error);
		}
		
		for(int i=0;i<centroids;i++){
			for (int j=0;j<centroids;j++){
				if(i!=j && denom[i]>0 && dalgo.d [i][j]<dalgo.INF )
					{
						dgraph.ODMatrix[i][j]= (float) Math.round ((TrafficProducedataNode[i] * Attraction[j]*Math.exp(-coeff*dalgo.d [i][j])/denom[i] ));

					}
				else
					dgraph.ODMatrix[i][j]=0;
				
			}
		}

	}
	
	public void printODMatrix(DirectedGraph dgraph){
		
		PrintWriter flowoutput=null;
		int centroids=dgraph.Centroids() ;	
		try
			{
				flowoutput=new PrintWriter(new FileOutputStream("TC1990_od1.txt"));
			}				  		
		catch(IOException e)
			{
				System.out.print("Error opening the files!");
				System.exit(0);
			
			}
		flowoutput.print(centroids*centroids+"\n");
		for(int i=0;i<centroids;i++){
			
			for(int j=0;j<centroids;j++){
				
				flowoutput.print((i+1)+"\t");
				flowoutput.print((j+1)+"\t");
				flowoutput.print(dgraph.ODMatrix[i][j]+"\n");
			}
			//flowoutput.print("\n");
		}
		flowoutput.print("\n");

		flowoutput.close();
		System.out.print("ODMatrix created.\n");

	}	
	
	public void printODCost(DirectedGraph dgraph,DijkstrasAlgo dalgo){
		
		PrintWriter flowoutput=null;
		int centroids=dgraph.Centroids() ;	
		try
			{
				flowoutput=new PrintWriter(new FileOutputStream("odcost.txt"));
			}				  		
		catch(IOException e)
			{
				System.out.print("Error opening the files!");
				System.exit(0);
			
			}
		for(int i=0;i<centroids;i++){
			
			for(int j=0;j<dgraph.Vertices() ;j++){
				
				flowoutput.print((i+1)+"\t");
				flowoutput.print((j+1)+"\t");
				flowoutput.print(dalgo.d[i][j]+"\n");
			}
			//flowoutput.print("\n");
		}
		flowoutput.print("\n");

		flowoutput.close();
		System.out.print("ODCost created.\n");

	}	
	
}