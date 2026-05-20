//Created by Feng Xie; Complemented by Shanjiang Zhu

import java.io.*;
import java.text.*;
public class SONG {
	
	SONG() throws IOException{

	}
	
	public static void main(String[] args) throws IOException, InterruptedException{
		
		
		System.out.print("SONG2.1.0.2, powered by NEXUS, is developed to predict the growth of the Twin Cities road network.\n");

		System.out.print("\nhis model has been calibrated. The calibration process starts from the 1990 network (Linkinfo_1990m), \nwith exogenous 1990,1995,2000,2005 link capacities (read from Linkinfo_1990m, 1995m, 2000m, 2005m .txt, respectively), \nruns the travel demand model without investment and calibrates to match the station counts in 2005.\n");

		System.out.print("\nThis version,created for Scenario 7 starts from the 2005 network incorporating existing links, legacy links, and potential links (Linkinfo_2005.txt).\n");
		System.out.print("Note that the network files Linkinfo_2005.txt for Scenario 1-6 are different from that for Scenario 7.\n");
		
		System.out.print("\nProgram started...\n");

		DirectedGraph dg;
		TGeneration tgen;
		DijkstrasAlgo dalgo;	
		TDistribution tdist;		
		TAssignment tassign;
		Investment invest;
		int scenarioID;  //Alternative scenarioID for investment Model
		
		dg=new DirectedGraph();
		tgen=new TGeneration(dg);
		dalgo=new DijkstrasAlgo(dg);
		tdist=new TDistribution(dg);
		tassign=new TAssignment(dg);
		invest=new Investment(dg);
		
		scenarioID = 7;
		
		//Calculate free flow OD travel time and save it for MOE_Equity;
		dg.generateOppoParallel();
		dg.generateParallel();
		dg.generateDensity();
		dg.updateGraph();
		tassign.FreeFlowTravelTime(dg,dalgo);
		dg.updateBPRtt() ;
		
		if(dg.linkinfofile=="Linkinfo_1990n.txt"){
			dg.updateBPRtt() ;
		}
		
		if (scenarioID==6 || scenarioID==7){
			dg.loadMCD();
			dg.loadFlow();
		}
		
		//System.out.print("Before the program starts:\n");
		//for(int i=0;i<dg.Edges();i++){
		//	if((i+1)%1000==0)System.out.print(dg.link_info [i][9]+"\t");
		//}
		//System.out.print("\n");
		
		System.out.println("\tScenarioID:"+scenarioID);
		System.out.print("\tTrip generation multiplier= "+tgen.trip_gen_mutiplier+"\n" );		
		System.out.print("\tFriction factor= "+tdist.coeff /60+"\n");
		
		int tPeriod=5;
				
		int t;		
		for (int k=0;k<=tPeriod;k++){
			t=5*k;

			System.out.print("\n");
			System.out.print("------------------------------------YEAR "+(dg.baseyear +t)+"------------------------------------\n");
			dg.updateGraph();
			System.out.print("**Trip Generation started...\n");		
			tgen.updateDemoInfo(dg,dg.baseyear +t );
			tgen.tripGeneration(dg);
			System.out.print("**Shortest path finding...\n");
			dalgo.dijkstrasalgo(dg,dg.BPRtt() );			
			System.out.print("**Trip Distribution started...\n");							
			tdist.tripDistribution(dg,tgen,dalgo);
			if((dg.baseyear+t)==1000){
				tdist.printODMatrix(dg);
				tdist.printODCost( dg,dalgo);
			}
			System.out.print("**Traffic Assignment started...\n");				
			tassign.trafficassignment(dg,dalgo,dg.baseyear +t);
			tassign.MOEs( dg,tgen.totaltrips/2 ,tdist.coeff ,dalgo.d, k);
			if(dg.baseyear+t>=2005){//Investment starts since 2005
				System.out.print("**Investment started...\n");						
				invest.ranking( dg,dg.baseyear +t,scenarioID);
				invest.investing( dg,dg.baseyear +t,scenarioID);				
			}
			dg.outputLinkInfo( dg.baseyear +t);
			System.out.print("--------------------------------------------------------------------------------------------------\n");		
			System.out.print("\n");
		}				
		
		tassign.finalMOEs( dg,tgen.totaltrips/2 ,tdist.coeff ,dalgo.d);
		System.out.print("Program ended.\n");
		
	}
}