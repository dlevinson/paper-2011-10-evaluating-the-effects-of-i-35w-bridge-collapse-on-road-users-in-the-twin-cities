import java.text.*;

public class TGeneration {
	float TrafficProducedataNode[];   //// from a centroid
	float TrafficAttractedtoaNode[];    //// to a cnetroid
	int extTrips[];/// external stations
	double ext_Betas_1990[];///from 1990 to 2000
	int vertices;
	int centroids ;
	int taz;
	public double trip_gen_mutiplier=1.0;
	public float totaltrips=0;
	
	public TGeneration( DirectedGraph dgraph) {
		vertices=dgraph.Vertices() ;
		centroids=dgraph.Centroids() ;
		taz=dgraph.TAZ() ;
		TrafficProducedataNode=new float [centroids];
		TrafficAttractedtoaNode=new float [centroids];
		for(int i=0;i<centroids;i++){
			TrafficProducedataNode[i]=TrafficAttractedtoaNode[i]=0;
		}
		
		
		//1990 External Station AADT (From MetCouncil)
		extTrips=new int [centroids-taz];

		extTrips[0]=11300;extTrips[1]=4200;extTrips[2]=26500;extTrips[3]=6200;extTrips[4]=13800;
		extTrips[5]=3600;extTrips[6]=15000;extTrips[7]=44000;extTrips[8]=9200;extTrips[9]=4550;
		extTrips[10]=3700;extTrips[11]=1550;extTrips[12]=1750;extTrips[13]=11100;extTrips[14]=1200;
		extTrips[15]=5000;extTrips[16]=1800;extTrips[17]=21000;extTrips[18]=3200;extTrips[19]=3900;
		extTrips[20]=10100;extTrips[21]=2700;extTrips[22]=2400;extTrips[23]=5400;extTrips[24]=5000;
		extTrips[25]=1450;extTrips[26]=11300;extTrips[27]=1600;extTrips[28]=11300;extTrips[29]=4800;
		extTrips[30]=36500;extTrips[31]=18000;extTrips[32]=20100;extTrips[33]=2650;extTrips[34]=3300;
		//Betas		
		ext_Betas_1990=new double[centroids-taz];	
		for(int i=0;i<centroids-taz;i++)
			ext_Betas_1990[i]=0.02;
		//ext_Betas_1990[0]=0.034741501;ext_Betas_1990[1]=0.053903643;ext_Betas_1990[2]=0.052011967;ext_Betas_1990[3]=0.070905976;ext_Betas_1990[4]=0.047742014;
		//ext_Betas_1990[5]=0.062488269;ext_Betas_1990[6]=0.005209496;ext_Betas_1990[7]=0.046019164;ext_Betas_1990[8]=0.035179237;ext_Betas_1990[9]=0.003248794;
		//ext_Betas_1990[10]=0.065834151;ext_Betas_1990[11]=0.075181611;ext_Betas_1990[12]=0.02991879;ext_Betas_1990[13]=0.052409779;ext_Betas_1990[14]=0.04423693;
		//ext_Betas_1990[15]=0.056951172;ext_Betas_1990[16]=0.045173892;ext_Betas_1990[17]=0.050896583;ext_Betas_1990[18]=0.041379744;ext_Betas_1990[19]=0.034974878;
		//ext_Betas_1990[20]=0.023171029;ext_Betas_1990[21]=0.030606578;ext_Betas_1990[22]=0.030782916;ext_Betas_1990[23]=0.038779411;ext_Betas_1990[24]=0.038568833;
		//ext_Betas_1990[25]=0.042570586;ext_Betas_1990[26]=0.01949209;ext_Betas_1990[27]=0.034680474;ext_Betas_1990[28]=0.0470742;ext_Betas_1990[29]=0.037706069;
		//ext_Betas_1990[30]=0.050959157;ext_Betas_1990[31]=0.060867355;ext_Betas_1990[32]=0.044279046;ext_Betas_1990[33]=0.04959622;ext_Betas_1990[34]=0.073386395;
	}
	
	public void updateDemoInfo(DirectedGraph dgraph,int year){
		
		extTrips[0]=11300;extTrips[1]=4200;extTrips[2]=26500;extTrips[3]=6200;extTrips[4]=13800;
		extTrips[5]=3600;extTrips[6]=15000;extTrips[7]=44000;extTrips[8]=9200;extTrips[9]=4550;
		extTrips[10]=3700;extTrips[11]=1550;extTrips[12]=1750;extTrips[13]=11100;extTrips[14]=1200;
		extTrips[15]=5000;extTrips[16]=1800;extTrips[17]=21000;extTrips[18]=3200;extTrips[19]=3900;
		extTrips[20]=10100;extTrips[21]=2700;extTrips[22]=2400;extTrips[23]=5400;extTrips[24]=5000;
		extTrips[25]=1450;extTrips[26]=11300;extTrips[27]=1600;extTrips[28]=11300;extTrips[29]=4800;
		extTrips[30]=36500;extTrips[31]=18000;extTrips[32]=20100;extTrips[33]=2650;extTrips[34]=3300;
		
		//update the demographic data by TAZs
		int deltayear=year-1990;
		if (deltayear%10==0){
			for (int i=0;i<taz;i++){
				for (int j=0;j<9;j++){
					dgraph.TAZ_info[i][j]=dgraph.TAZ_info_forecasted [i][j][(int)(deltayear/10)];
				}
			}		

		}
		else{
			int min=(int)(deltayear-deltayear%10)/10;
			int max=min+1;
			for (int i=0;i<taz;i++){
				for (int j=0;j<9;j++){
					dgraph.TAZ_info[i][j]=(float)(dgraph.TAZ_info_forecasted [i][j][min]*((10-deltayear%10)*0.1)+dgraph.TAZ_info_forecasted [i][j][max]*((deltayear%10)*0.1));
				}
			}		

		}
		
		
		//update the external station traffic
		
		for(int i=0;i<centroids-taz;i++){
			extTrips[i]*=Math.pow((1+ext_Betas_1990[i]),deltayear);
		}
		
	}
	
	public void tripGeneration(DirectedGraph dgraph) {
		float convertionratio=dgraph.convertionratio;		
		//update the demographic data by Counties
		for (int i=0;i<8;i++){
			dgraph.juris_info[i][0]=0;
		}
		
		int m[];
		m=new int[8];
		for (int i=0;i<8;i++)
			m[i]=0;	
		for (int i=0;i<taz;i++){
			int county=(int)dgraph.TAZ_info[i][6];
				//if(county<0)System.out.print("i="+i+"\tcounty="+county+"\n");
			//number of household
			dgraph.juris_info[0][0]+=dgraph.TAZ_info[i][0];//state
			dgraph.juris_info[county][0]+=dgraph.TAZ_info[i][0];//county				
		}		
		
		
	 //version A:production and attraction combined
		//Intercept	35.93885022
		//POPULATION	0.12922701
		//RETAIL	0.232498334
		//NON RETAIL	0.149062479
		//RES DENSITY	-0.086487052
		//DISTANCE	0.00795244
		//DISTANCE SQ	-2.13347E-07  
	   
	   //this regression model uses the total generation and attraction, so the calculated number
	   //should be divided by 2
	   /*
		for(int i=0;i<taz;i++){
			//version A:
			TrafficProducedataNode[i]=(float)(35.9389+0.1292*dgraph.TAZ_info [i][2]+0.2325*dgraph.TAZ_info [i][7]+0.1491*dgraph.TAZ_info [i][8])/2;
			TrafficProducedataNode[i]+=(float)(-0.086487*dgraph.TAZ_info [i][3]+0.0079524*dgraph.TAZ_info [i][5]-2.13347E-7*Math.pow(dgraph.TAZ_info [i][5],2))/2;								
			
			if(TrafficProducedataNode[i]<0) TrafficProducedataNode[i]=0;
			
			TrafficProducedataNode[i]*=1.15;
			
			TrafficAttractedtoaNode[i]=TrafficProducedataNode[i];			
			
			total_trips+=2*TrafficProducedataNode[i];
		}
		
		*/
		//version B:production and attraction separated
		//Production
		//Intercept: -14.899933
		//POPULATION	0.108304451
		//RETAIL	0.03480615
		//NON RETAIL	0.011022643
		//RES DENSITY	-0.000320247
		//DISTANCE	0.005206677
		//DISTANCE SQ	-1.20079E-07  
		
		//Atrraction
		//Intercept	50.83878331
		//POPULATION	0.020922559
		//RETAIL	0.197692184
		//NON RETAIL	0.138039836
		//RES DENSITY	-0.086166805
		//DISTANCE	0.002745763
		//DISTANCE SQ	-9.32674E-08  
		
		float total_production=0,total_attraction=0;	   

		for(int i=0;i<taz;i++){
			//version B:
			TrafficProducedataNode[i]=(float)(-14.899933+0.108304451*dgraph.TAZ_info [i][2]+0.03480615*dgraph.TAZ_info [i][7]+0.011022643*dgraph.TAZ_info [i][8]);
			TrafficProducedataNode[i]+=(float)(-0.000320247*dgraph.TAZ_info [i][3]+0.005206677*dgraph.TAZ_info [i][5]-1.20079E-07*Math.pow(dgraph.TAZ_info [i][5],2));											
			if(TrafficProducedataNode[i]<0) TrafficProducedataNode[i]=0;	
			TrafficProducedataNode[i]*=trip_gen_mutiplier;	
			total_production+=TrafficProducedataNode[i];	
				
			TrafficAttractedtoaNode[i]=(float)(50.83878331+0.020922559*dgraph.TAZ_info [i][2]+0.197692184*dgraph.TAZ_info [i][7]+0.138039836*dgraph.TAZ_info [i][8]);
			TrafficAttractedtoaNode[i]+=(float)(-0.086166805*dgraph.TAZ_info [i][3]+0.002745763*dgraph.TAZ_info [i][5]-9.32674E-08*Math.pow(dgraph.TAZ_info [i][5],2));
			if(TrafficAttractedtoaNode[i]<0) TrafficAttractedtoaNode[i]=0;			
			TrafficAttractedtoaNode[i]*=trip_gen_mutiplier;					
			total_attraction+=TrafficAttractedtoaNode[i];		
			
		}
		
		//System.out.print(total_production+"\t"+total_attraction+"\n");
		if(total_production!=total_attraction){
			for(int i=0;i<taz;i++){
				TrafficAttractedtoaNode[i]=TrafficAttractedtoaNode[i]*(total_production/total_attraction);
			}
		}
		totaltrips=2*total_production;
		//System.out.print(total_trips);
		DecimalFormat myFormatter = new DecimalFormat("#######.00");			
		System.out.print("\tTotal "+ myFormatter.format( (float)total_production/1000)+" thousand trips are produced by "+myFormatter.format(dgraph.juris_info[0][0]/1000) +" thousand households in the seven-county region in the morning peak hour.\n\n");
		//System.out.print(TrafficProducedataNode[189]+"\n");
		int trip_generation_juris[]=new int [8];
		for (int i=0;i<=7;i++){
			trip_generation_juris[i]=0;
		}
		for(int i=0;i<taz;i++){
			int county=(int)dgraph.TAZ_info [i][6];
			trip_generation_juris[county]+=TrafficProducedataNode[i];
			trip_generation_juris[0]+=TrafficProducedataNode[i];
		}
		//for (int i=0;i<=7;i++){
			
			//System.out.print("County "+i+":\t"+dgraph.juris_info  [i][0]+"\t"+trip_generation_juris[i]+"\n");
		//}
		
		//External Stations
		for (int i=0;i<centroids-taz;i++){
			TrafficProducedataNode[taz+i]=(float)((extTrips[i]/convertionratio)/2);//convert aadt to peak hour data
			TrafficAttractedtoaNode[taz+i]=TrafficProducedataNode[taz+i];//???
		}
				
		/*
		System.out.print("#Trips generated (Zones 1-1200):\n");
		for (int i=0;i<centroids;i++){
			System.out.print((int)TrafficProducedataNode[i]+"\t");
			if((i+1)%12==0)System.out.print("\n");
		}
		System.out.print("\n");
		*/
	}
	
	
	
}