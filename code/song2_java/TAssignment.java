
//import java.io.*;
import java.text.*;
public class TAssignment {
	public double x[];
	double xp[];	
	double bprtt[];
	
	
	float INF;
	int edges;
	double theta=0.2*60;//0.2 is for travel time in minutes; .2*60 is for hours
	//The dispersion parameter theta is set at 0.2, following Leurent's (1995) work on 
	//case studies in the Paris metropolitan area. This means that if one route is shorter by five 
	//minutes than another, then approximately three out of four drivers will choose the first road. 
		
		
	//variables used in dial's algorithm	
	int vertices;
	int startnode,endnode,ed,linkid;//temporary variables
	double lk[],w[],x_org[],x_total[];
	double sumW[],sumX[];
	//sumW stores the sum of link weights (w) for all the links entering a node. if it is the origin node, sumW=1
	//sumX will be used for backward pass
	int rank[];
	
//	FreeFlowTravelTime of OD for the calculation of Equity
	double odFFT[][];
	double odDelay[][];
//  OD travel time and OD flow for calculating Consummer Surplus
	double lastODQ[][];
	double lastODT[][];
	double accumulateCS;
	
	public TAssignment( DirectedGraph dgraph) {
		//System.out.print("!!theta="+theta/60+"\n\n");	

		INF=dgraph.INF ;
		vertices=dgraph.Vertices() ;//total nodes
		edges=dgraph.Edges() ;//total links
		x = new double[edges];	//a vector to store the link flows
		bprtt= new double[edges];//a temporary array to store the bpr travel time of links for each iteration of MSA
		xp =  new double [edges];//a temporary array to store the flow pattern derived in the previous iteration of MSA
		lastODQ = new double[dgraph.Centroids()][dgraph.Centroids()]; // a temporary matrix to store the OD flow in previous iteration of MSA
		lastODT = new double[dgraph.Centroids()][dgraph.Centroids()];// a temporary matrix to store the OD travel time in previous iteration of MSA
		accumulateCS = 0; // variable to store the consumer surplus compared to base year.
		
		for(int i=0; i<edges ; i++) {
			x[i] =xp[i]=0;
			bprtt[i]=dgraph.link_info [i][9];
		}
		
		edges=dgraph.Edges() ;
		double theta=0.2*60;//0.2 is for travel time in minutes; .2*60 is for hours
		//The dispersion parameter theta is set at 0.2, following Leurent's (1995) work on 
		//case studies in the Paris metropolitan area. This means that if one route is shorter by five 
		//minutes than a second, then approximately three out of four drivers will choose the first road. 
		
		vertices=dgraph.Vertices() ;
		lk=new double [edges];// link likelihood
		w=new double [edges]; //link weight used in forward pass
		//x=new float [edges];//total link flows summing up all o-d pairs

		sumW=new double [vertices];
		sumX=new double[vertices];
		
		x_total=new double [edges];
		x_org=new double [edges];//link flows calculated for a specific origin (centroid) node
		
		rank=new int [vertices];
		for (int p=0;p<vertices;p++){
			rank[p]=p+1;//store node numbers
		}
		
	}


	public void trafficassignment(DirectedGraph dgraph, DijkstrasAlgo dal, int year) {
		dgraph.updateBPRtt() ;
		//All-or-nothing assignment 	
		
		/*
		int tempNode=0,leadingNode=0, followingNode=0;		
				
		for(int i=0; i<dgraph.Centroids (); i++) {	//// for each node of the graph as the origin of the shortest path
			for(int j=dgraph.Vertices()-1;j>=1; j--) {      
				//// for each element from the end of the shortest path
				//// find its previously connected permanent node along the shortest path until the orign ithNode is reached
				followingNode=dal.s[i][j];  //Node Number of the element in the permanent vector
				//System.out.println(followingNode+"follow\n");	
				if(followingNode<1){
				}
					
				else{
					leadingNode=dal.pi[i][followingNode-1];// the predecessor,which is DIRECTLY connected to the node 
					
					
					do
					{
						
						int K=-1;
					a:	for(int k=0;k<dgraph.NoofLinks(leadingNode);k++){
							if(dgraph.EndNodeNumbers( leadingNode, k+1 )==followingNode)
								{K=k;break a;} 
								
						}
						
						//System.out.println("i+1="+(i+1)+" following="+followingNode+" leadingNode="+leadingNode+" K="+K);
							
							x[dgraph.linkID [leadingNode-1][K]-1]+= dgraph.ODMatrix (i+1,dal.s[i][j]); ////
							//if(period==0)System.out.print("traffic="+TrafficonaLink[leadingNode-1].access(K)+"\tODMatrix="+ODMatrix(i, dalgo.s[i][j]-1, (float)dalgo.pLabel(i+1, dalgo.s[i][j]) )+"\tdenom="+denom[i]+"\n");
							followingNode=leadingNode;
							leadingNode=dal.pi[i][followingNode-1];							
						

					}	
					while(followingNode!=i+1);
						
				}
			}
						
		}
		
		System.out.print("F:\t");
		for(int p=0; p<dgraph.Edges() ; p++) {
			System.out.print(x[p]+"\t");
		}			
		System.out.print("\n");
		*/
		
		
	//Stochastic User Equilibrium (SUE)
			
		//initialization
		//int edges=dgraph.Edges() ;
		int startnode,endnode;//temporary variables
		//DijkstrasAlgo dal;		
		//dal=new DijkstrasAlgo(dgraph);
		
		//Specify convergence requirement
		int iternum=0,maxiternum=100;
		double errorcrit=100.0;		
		
		if(year>2020){
			maxiternum=100;
			errorcrit=200;
		}
		
		double error[];
		error=new double[maxiternum+1]; // a vector to store the error term (dependent on definition),showing the trend of convergence
		for(int i=0; i<=maxiternum ; i++) {
			error[i]=0;
		}

		//change2
		for(int i=0; i<edges ; i++) {
			x[i]=dgraph.link_info [i][8];
			//if((i+1)%1000==0)System.out.print(x [i]+"\t");
			x[i]=Math.round( 100*x[i])/100;
			//if((i+1)%1000==0)System.out.print(x[i]+"\t");
		}		
		System.out.print("\n");
		
		//change3
		for (int p=0;p<vertices;p++){
			rank[p]=p+1;//store node numbers
		}
		
		//System.out.print("	Initialization finished\n");
		//Refer to Sheffi's book p.327
		//0) Stochastic network loading based on a set of initial travel times
		
		//System.out.print("Network loading before MSA...\n");
		
		int linkno=-1;			
					
		do {		
		
			iternum=iternum+1;
			//System.out.print("\nMSA: Iteration "+ iternum+"\n");
			//0) store the flow pattern at the begin of a MSA iteration
			for(int p=0; p<edges; p++) {
				xp[p]=x[p];
			}
			
			//1) update link travel times
			//According to the BPR function,link travel time=free flow travel time*(1+0.15*(flow/capacity)**4)
			for(int p=0; p<edges; p++) {
				double vcratio=0;
				if(bprtt[p]<INF && dgraph.link_info [p][7]!=0){
					vcratio=x[p]/dgraph.link_info [p][7];
					if(dgraph.link_info[p][5]!=0)
					{
						bprtt[p]=(dgraph.link_info[p][4]/dgraph.link_info[p][5])*(1+0.15*Math.pow( vcratio,4.0));

						//if(vcratio<=1)bprtt[p]=(dgraph.link_info[p][4]/dgraph.link_info[p][5])*(1+0.15*Math.pow( vcratio,1.0));
						//else if(vcratio>1)bprtt[p]=(dgraph.link_info[p][4]/dgraph.link_info[p][5])*(1+0.15*Math.pow( vcratio,0.5));

					}
					else bprtt[p]=INF;
					
					if(bprtt[p]>dgraph.threshold_tt)bprtt[p]=dgraph.threshold_tt;
				}
			}		

			// 2)  perform a new stochastic network loading procedure based on updated link travel times.
			//find the new flow pattern
				//dal.dijkstrasalgo(dgraph,bprtt);
				x=DialsAlgo(dgraph,dal.d,bprtt);	

			// 3) move			
				for(int p=0; p<edges; p++) {
					double diff=x[p]-xp[p];
					startnode=(int)dgraph.link_info [p][1];
					endnode=(int)dgraph.link_info [p][2];
					//if(iternum<=3&&(startnode==190||endnode==190||startnode==204||endnode==204||startnode==3131||endnode==3131||startnode==3133||endnode==3133))System.out.print((p+1)+"\t"+startnode+"\t"+endnode+"\t"+x[p]+"\n");
					if(Math.abs(diff)>error[iternum]){error[iternum]=Math.abs( diff);linkno=p;}
					int k=iternum%100;
					if (k==0)k=100;
					x[p]=xp[p]+(diff/(double)(k));
					
				}
			//4) convergence criterion: if convergence is attained, stop; if not, set n=n+1 and go to step 1)
			System.out.print("	MSA Iteration "+iternum+": Error="+error[iternum]+"\t"+(linkno+1)+"\n\n");
			
		}while(error[iternum]>errorcrit && iternum<maxiternum);

		//replace the link_info array with the resulted flow pattern (x)
		
		for(int p=0; p<edges; p++) {
			dgraph.link_info [p][8]=(float)x[p];
		}		

		//update the BPR travel time for each link
		dgraph.updateBPRtt() ;
		
		///predict the crash counts on links
		/*
		for (int p=0;p<edges;p++){
			float aadt=dgraph.link_info [p][8]*dgraph.convertionratio;
			float length=dgraph.link_info [p][4];
			int state=0,county=0,township=0;
			if(dgraph.link_info [p][17]==0)state=1;
			else if(dgraph.link_info [p][17]==1)county=1;
			else township=1;			
			dgraph.link_info [p][12]=(aadt*length)*aadt*(float)Math.exp(-15.4744-0.9595655*length-0.0004853*aadt*length+9.933467*township+(3.387386+4.125646)/2*county+(1.711183+2.501844+2.756652)/4*state);
		}
		*/
		///Calibration using peak hour volumes
		float real_aver=0,forecast_aver=0,RMSE=0;//volume
		for(int i=0;i<63;i++){
			forecast_aver+=dgraph.link_info [dgraph.stations [i]-1][8];
			real_aver+=dgraph.station_volumes [i];
		}
		forecast_aver/=63;
		real_aver/=63;
		for(int i=0;i<63;i++){
			RMSE+=Math.pow((dgraph.link_info [dgraph.stations [i]-1][8]-dgraph.station_volumes [i]),2);
		}
		RMSE=(float) Math.sqrt( RMSE/62);
		System.out.print("Aver peak volume="+forecast_aver+", which is "+(100-100*forecast_aver/real_aver)+" percent off.\n");
		System.out.print("Percent RMSE="+100*RMSE/real_aver+" percent.\n");
		
		System.out.print("LinkID\tLinkType\tForecast\tCounts\n");	
		for(int i=0;i<63;i++){
			System.out.print(dgraph.stations [i]+"\t"+dgraph.link_info [dgraph.stations [i]-1][3]+"\t"+dgraph.station_volumes [i]+"\t"+dgraph.link_info [dgraph.stations [i]-1][8]+"\n");
		}
		
		//Calibration using aadt volumes
		/*
		float total_i94=0;
		//Calibration using I94 traffic
		for(int i=0;i<120;i++){
			total_i94+=dgraph.link_info [dgraph.I94 [i]-1][8]*dgraph.link_info [dgraph.I94 [i]-1][4];
		}
		System.out.print("Daily vmt on I94 is "+total_i94*dgraph.convertionratio +"\n");

		float total_35E=0;
		//Calibration using I94 traffic
		for(int i=0;i<112;i++){
			total_35E+=dgraph.link_info [dgraph.I35E [i]-1][8]*dgraph.link_info [dgraph.I35E [i]-1][4];
		}
		System.out.print("Daily vmt on I35E is "+total_35E*dgraph.convertionratio +"\n");

		float total_169=0;
		//Calibration using I94 traffic
		for(int i=0;i<98;i++){
			total_169+=dgraph.link_info [dgraph.H169 [i]-1][8]*dgraph.link_info [dgraph.H169 [i]-1][4];
		}
		System.out.print("Daily vmt on 169 is "+total_169*dgraph.convertionratio +"\n");


		float total_Mississipi=0;
		//Calibration using Mississipi bridge traffic
		for(int i=0;i<50;i++){
			total_Mississipi+=dgraph.link_info [dgraph.Mississipi[i]-1][8];
		}
		System.out.print("Daily Traffic across bridges on Mississipi River is "+total_Mississipi*dgraph.convertionratio +"\n");
		
		//for(int i=1; i<=maxiternum ; i++) {
		//	if(i<=5||i%10==0)System.out.print(i+"\t"+error[i]+"\n");
		//}		
		*/	
	}


	double[] DialsAlgo(DirectedGraph dgraph, double d[][],double bprtt[]){
		for(int p=0; p<edges; p++) {
			x_total[p]= 0;
		}
		System.out.print("	Dial's Algorithm running...0%");
		//Dial's algorithm
		for(int i=0;i<dgraph.Centroids() ;i++){
			if((i+1)%48==0){
				System.out.print(".");
				if((i+1)%240==0){
					System.out.print((i+1)/12+"%");
				}	
			}
			int origin=i+1;

			//calculate link likelihoods
			
			for(int p=0; p<edges; p++) {
				lk[p] =w[p]=x_org [p]=0;
				
				startnode=(int)dgraph.link_info [p][1];
				endnode=(int) dgraph.link_info [p][2];
				  if(d[origin-1][startnode-1]<d[origin-1][endnode-1] && d[origin-1][endnode-1]<INF)//dalgo.d[][] stores the O-D travel time cost
					  lk[p]=(float) Math.exp( theta*(d[origin-1][endnode-1]-d[origin-1][startnode-1]-bprtt[p]));				  
				  else lk[p]=0;
			}
			
			/*
			System.out.print("lk:\t");
				for(int p=0; p<edges; p++) {
					System.out.print(lk[p]+"\t");
				}			
				System.out.print("\n");
			*/
			
			//Forward pass
				//Sort vertices ascendingly according to their distances to the origin node (i.e.,dalgo.d[origin-1][nd-1]))
				
				for(int p=0;p<vertices-1;p++){
					for(int q=p+1;q<vertices;q++){
						if(d[origin-1][rank[p]-1]>d[origin-1][rank[q]-1]){
							int temp=rank[p];
							rank[p]=rank[q];
							rank[q]=temp;
						}
					}
				}
				
				//Calculate link weights 
				//( This is the most time-consuming part, maybe because it really calculates 8,000*20,000 times for TC network)
				for (int p=0;p<vertices;p++){
					sumW[p]=sumX[p]=0;
				}
				/*less efficient code
				sumW[origin-1]=1;					
				for(int p=1;p<vertices;p++){//rank[0] must be the origin node, and it doesn't need to be examined
					ed=rank[p];//node to be examined					
					for(int q=0; q<edges; q++) {
						startnode=(int)dgraph.link_info [q][1];
						endnode=(int) dgraph.link_info [q][2];
						if (endnode==ed){// if the examined link enters the examined node							
							w[q]=lk[q]*sumW[startnode-1];
							sumW[endnode-1]+=w[q];
						}	
					}
				}
				*/
				
				sumW[origin-1]=1;					
				for(int p=1;p<vertices;p++){//rank[0] must be the origin node, and it doesn't need to be examined
					ed=rank[p];//node to be examined

						for(int q=1;q<=dgraph.endnodeTolinks[ed-1][0];q++ ){
							linkid=dgraph.endnodeTolinks[ed-1][q];
							startnode=(int)dgraph.link_info[linkid-1][1]; 		
							w[linkid-1]=lk[linkid-1]*sumW[startnode-1];
							sumW[ed-1]+=w[linkid-1];							
						}

					
				}		
				/*		
			System.out.print("w:\t");
				for(int p=0; p<edges; p++) {
					System.out.print(w[p]+"\t");
				}			
				System.out.print("\n");
								
			//System.out.print("2\t");
				
			System.out.print( "\nsum of weights:\t");	
			for(int p=0; p<vertices; p++) {
				System.out.print(sumW[p]+"\t");
			}
			System.out.print( "\n");
			*/	
				
				
			//Backward pass								
				/*	
				for(int p=0; p<edges; p++) {
					x_org [p]=0;
				}			
				*/
				for(int p=vertices-1;p>0;p--){
					ed=rank[p];
					
						if(sumW[ed-1]!=0){
							double temp=dgraph.ODMatrix(origin,ed)+sumX[ed-1];
							if(temp!=0){
								for(int q=1;q<=dgraph.endnodeTolinks[ed-1][0];q++ ){
									linkid=dgraph.endnodeTolinks[ed-1][q];
									if(w[linkid-1]!=0){
										startnode=(int)dgraph.link_info[linkid-1][1]; 	
								
										x_org[linkid-1]=temp*w[linkid-1]/sumW[ed-1];
										sumX[startnode-1]+=x_org[linkid-1];
									}
								}

								/*//less efficient code
								for(int q=0; q<edges; q++) {
									startnode=(int)dgraph.link_info [q][1];
									endnode=(int) dgraph.link_info [q][2];
									if (endnode==ed && w[q]!=0){// if the examined link enters the examined node
										x_org[q]=temp*w[q]/sumW[ed-1];
										sumX[startnode-1]+=x_org[q];
									}	
								}							
								*/	
							}
						}

						
				}
			/*	
			System.out.print( "\nsum of flows:\t");	
			for(int p=0; p<vertices; p++) {
				System.out.print(sumX[p]+"\t");
			}
			System.out.print( "\n");	
			*/
			//System.out.print("x_org:\n");		
			
			for(int p=0; p<edges; p++) {
				x_total[p]+=x_org[p];
				//System.out.print(x_org[p]+"\t");
			}	
		
		}
		System.out.print("\n");
		return x_total;
	}
	
public void MOEs(DirectedGraph dgraph,float tripproduced,double friction_factor, double d[][], int tPeriods){
		
		float vht,vkt,accessibility,aver_trip_length,aver_trip_time;
		double cs;
		vht=0;
		vkt=0;
		accessibility=0;
		cs = 0;
		
		System.out.print("Trips produced="+tripproduced+"\n");
		for(int p=0; p<edges; p++) {
			vht+=365*dgraph.convertionratio*dgraph.link_info [p][9]*dgraph.link_info [p][8];
			vkt+=365*dgraph.convertionratio*(dgraph.link_info[p][4]*1.609)*dgraph.link_info [p][8];
		}			
		aver_trip_length=(vkt/(365*dgraph.convertionratio))/tripproduced;
		aver_trip_time=(vht/(365*dgraph.convertionratio))/tripproduced;
		
		float taz_access=0;
		for (int i=0;i<dgraph.TAZ();i++){
			taz_access=0;
			for(int j=0;j<dgraph.TAZ();j++){
				if(i!=j){				
					if(d[i][j]!=0)
					taz_access+=(dgraph.TAZ_info [j][7]+dgraph.TAZ_info [j][8])*Math.exp (-friction_factor*d[i][j]);
				}
			}
			accessibility+=(dgraph.TAZ_info [i][7]+dgraph.TAZ_info [i][8])*taz_access;
		}
		
		// Calculate Consummer Surplus?
		if (tPeriods == 0)
		{
			for (int i=0;i<dgraph.Centroids();i++)
			{
				for (int j=0;j<dgraph.Centroids();j++)
				{
					lastODT[i][j] = d[i][j];
					lastODQ[i][j] = dgraph.ODMatrix(i+1,j+1);
				}
			}
		}else{
			cs = 0;
			for (int i=0;i<dgraph.Centroids();i++)
			{
				for (int j=0;j<dgraph.Centroids();j++)
				{
					cs = cs +0.5*(dgraph.ODMatrix(i+1,j+1) + lastODQ[i][j])*(lastODT[i][j] - d[i][j]);
						//Consummer Surplus for each OD pair;
					lastODT[i][j] = d[i][j];					//Update matrix for OD travel time;
					lastODQ[i][j] = dgraph.ODMatrix(i+1,j+1); //Update matrix for OD flow;
				}
			}
			accumulateCS = accumulateCS + cs;
		}
		//End of Consummer Surplus;
		
		DecimalFormat myFormatter = new DecimalFormat("0.000");
		System.out.print("**MOE Outpus:\n");
		System.out.print("vht\t vkt \n");
		System.out.print(vht+"\t"+vkt+"\n");
		System.out.print("trip length\ttrip time\tAccessibility\n");
		System.out.print(aver_trip_length+"\t"+aver_trip_time+"\t"+accessibility+"\n");
		System.out.print("Consumer Surplus for this time period\tAccumulate Consumer Surplus"+cs+"\t"+accumulateCS);
	}
	
	public void FreeFlowTravelTime (DirectedGraph dgraph, DijkstrasAlgo dal){
		System.out.println("\tCalculate Free Flow OD Travel Time:\n");
		odFFT = new double[dgraph.Centroids()][dgraph.Centroids()];
		//Chang current link flow to zero;
		int noofLink = dgraph.Edges();
		int noofCentroids = dgraph.Centroids();
		float templink_info8[],templink_info9[];
		templink_info8 = new float[noofLink];//store the link flow
		templink_info9 = new float[noofLink];//store the link travel time
		for (int i=0;i<noofLink;i++)
		{
			templink_info8[i] = dgraph.link_info[i][8]; //Store original link flow
			dgraph.link_info[i][8] = 0;
			
			templink_info9[i] = dgraph.link_info[i][9];// Store original link travel time 
			dgraph.link_info[i][9] = 0;

		}
		dgraph.updateBPRtt();          //Calculate Free Flow Travel Time for each Link;
		//Use Dijkstra Alogrithm to find the FFT of each OD;
		dal.dijkstrasalgo(dgraph);
		//System.out.println("OD free flow travel time:");
		for (int i=0;i<noofCentroids;i++)
		{
			for (int j=0;j<noofCentroids;j++)
			{
				odFFT[i][j]=dal.d[i][j];
			//System.out.print(""+odFFT[i][j]+"\t");
			}
			//System.out.print("\n");
		}
		//Restore the data
		for (int i=0;i<noofLink;i++)
		{
			dgraph.link_info[i][8] = templink_info8[i]; 
			dgraph.link_info[i][9] = templink_info9[i]; 
		}
		//dgraph.updateBPRtt();
		//System.out.println("****************End of FFT Algorithm************");
	}
	
	public void finalMOEs (DirectedGraph dgraph,float tripproduced,double friction_factor, double d[][])
	{
		float vht,vkt,accessibility,aver_trip_length,aver_trip_time,gini;
		vht=0;
		vkt=0;
		accessibility=0;
		
		System.out.print("Trips produced="+tripproduced+"\n");
		for(int p=0; p<edges; p++) {
			vht+=365*dgraph.convertionratio*dgraph.link_info [p][9]*dgraph.link_info [p][8];
			vkt+=365*dgraph.convertionratio*(dgraph.link_info[p][4]*1.609)*dgraph.link_info [p][8];
		}			
		aver_trip_length=(vkt/(365*dgraph.convertionratio))/tripproduced;
		aver_trip_time=(vht/(365*dgraph.convertionratio))/tripproduced;
		
		float taz_access=0;
		for (int i=0;i<dgraph.TAZ();i++){
			taz_access=0;
			for(int j=0;j<dgraph.TAZ();j++){
				if(i!=j){				
					if(d[i][j]!=0)
					taz_access+=(dgraph.TAZ_info [j][7]+dgraph.TAZ_info [j][8])*Math.exp (-friction_factor*d[i][j]);
				}
			}
			accessibility+=(dgraph.TAZ_info [i][7]+dgraph.TAZ_info [i][8])*taz_access;
		}
		
//		Euqity MOE
		System.out.println("***************Start Equity******************");
		int noofCentroids = dgraph.Centroids();
		float odTrips[][];
		double vectorDelay[];
		float vectorODTrips[];
		vectorDelay = new double[noofCentroids*noofCentroids];
		vectorODTrips = new float[noofCentroids*noofCentroids];
		odTrips =  new float[noofCentroids][noofCentroids];
		odDelay = new double[noofCentroids][noofCentroids];
		double totalDelay;
		float totalTrips;
		
			//Delay for each OD pair;
		System.out.println("    Delay for OD pairs");
		int k=0;
		totalDelay = 0;
		totalTrips = 0;
		for (int i=0;i<noofCentroids;i++)
		{
			for (int j=0;j<noofCentroids;j++)
			{
				odDelay[i][j] = d[i][j] - odFFT[i][j];
//				System.out.println(""+odDelay[i][j]+"\t"+d[i][j]+"\t"+odFFT[i][j]);
				odTrips[i][j] = dgraph.ODMatrix(i+1,j+1); //ODMatrix store number from (0,0), but OD ID from (1,1);
				//Convert Matrix to Vector for further sort;
				vectorODTrips[k]=odTrips[i][j];
				vectorDelay[k] = odDelay[i][j];
				k++;
				totalDelay = totalDelay + odDelay[i][j]*odTrips[i][j];
				totalTrips = totalTrips + odTrips[i][j];
			}
//			System.out.print("\n");
		}
		System.out.println("Total Delay:"+totalDelay);
		System.out.println("Total OD Trips:"+totalTrips);
			//Bubble Sort
		System.out.println("\t\tBubble Sort Started:");
		int noofCentroidsSquare = noofCentroids*noofCentroids;
		double tempDelay;
		float tempODTrips;
		System.out.print("Progress");
		for (int i=0;i<noofCentroidsSquare;i++)
		{
			if ((i+1)%50000 == 0)
			{
				System.out.print(".");
			}
			for (int j=0;j<noofCentroidsSquare-i-1;j++)
			{
				if (vectorDelay[j]>vectorDelay[j+1])
				{
					tempDelay = vectorDelay[j+1];
					vectorDelay[j+1] = vectorDelay[j];
					vectorDelay[j] = tempDelay;
					tempODTrips = vectorODTrips[j+1];
					vectorODTrips[j+1] = vectorODTrips[j];
					vectorODTrips[j] = tempODTrips;
				}
			}
		}
		System.out.println("\t\tBubble Sort Ended:");
			//Calculate A1 in Lorenz Curve;
		double a1,a1a2;
		double accumulateDelay;
		float accumulateODTrips;
		a1 = 0;
		accumulateODTrips = 0;
		accumulateDelay = 0;
		for (int i=0;i<noofCentroidsSquare;i++)
		{
			a1 = a1 + (accumulateODTrips/totalTrips*totalDelay-accumulateDelay)*vectorODTrips[i];
			accumulateODTrips = accumulateODTrips + vectorODTrips[i];
			accumulateDelay = accumulateDelay + vectorDelay[i]*vectorODTrips[i];
		}
		a1a2 = totalDelay*totalTrips/2;
		gini = (float)(a1/a1a2);
		System.out.print("a1:"+a1);
		System.out.print("\ta1a2"+a1a2);
		System.out.print("\tGini"+gini);
		System.out.println("*******************End of Equity**************");
		//End of Equity indicator, Gini coefficient
		
		DecimalFormat myFormatter = new DecimalFormat("0.000");
		System.out.print("**MOE Outpus:\n");
		System.out.print("trip length\ttrip time\tAccessibility\n");
		System.out.print(aver_trip_length+"\t"+aver_trip_time+"\t"+accessibility+"\n");
	}
}