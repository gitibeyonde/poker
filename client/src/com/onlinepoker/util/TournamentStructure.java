package com.onlinepoker.util;

public class TournamentStructure{
	public static double [][] _t_payout = {
		      { 66.7, 33.33}, //0
		      { 54.55, 27.27, 18.18 }, //1
		      { 49.18, 24.59, 16.39, 9.84}, //2
		      { 45.45, 22.73, 15.15, 9.09, 7.58}, //3
		      { 0, 2, 6, 8  }, //4
		      { 0, 2, 5, 6, 8  }, //5
		      { 0, 2, 3, 5, 6, 8 }, //6
		      { 0, 1, 2, 3, 5, 6, 8 }, //7
		      { 0, 1, 2, 3, 5, 6, 7, 8 },//8
		      { 0, 1, 2, 3, 5, 6, 7, 8, 9 }, //9
		      { 34.36, 17.18, 11.45, 6.87, 5.73, 4.91, 4.29, 3.82, 3.44, 2.65, 2.65}, // 10
		  };
	
//	66.67	33.33																															
//	54.55	27.27	18.18																														
//	49.18	24.59	16.39	9.84																													
//	45.45	22.73	15.15	9.09	7.58																												
//	42.68	21.34	14.23	8.54	7.11	6.1																											
//	40.52	20.26	13.51	8.1	6.75	5.79	5.07																										
//	38.76	19.39	12.93	7.76	6.46	5.54	4.85	4.31																									
//	37.33	18.66	12.44	7.47	6.22	5.33	4.67	4.15	3.73																								
//	36.2	18.1	12.07	7.24	6.03	5.17	4.53	4.02	3.62	3.02																							
//	35.23	17.61	11.74	7.04	5.87	5.03	4.4	3.91	3.51	2.83	2.83																						
//	34.36	17.18	11.45	6.87	5.73	4.91	4.29	3.82	3.44	2.65	2.65	2.65																					
//	33.58	16.79	11.2	6.72	5.6	4.8	4.2	3.73	3.36	2.59	2.59	2.59	2.25																				
//	32.89	16.45	10.97	6.58	5.48	4.7	4.11	3.66	3.28	2.54	2.54	2.54	2.13	2.13																			
//	32.26	16.14	10.76	6.45	5.38	4.61	4.03	3.59	3.25	2.49	2.49	2.49	2.02	2.02	2.02																		
//	31.73	15.85	10.57	6.34	5.28	4.53	3.96	3.52	3.17	2.45	2.45	2.45	1.98	1.98	1.98	1.76																	
//	31.19	15.59	10.39	6.24	5.2	4.45	3.9	3.46	3.12	2.41	2.41	2.41	1.95	1.95	1.95	1.69	1.69																
//	30.69	15.35	10.23	6.14	5.12	4.39	3.84	3.41	3.07	2.37	2.37	2.37	1.93	1.93	1.93	1.62	1.62	1.62															
//	27.75	13.88	9.25	5.55	4.63	3.96	3.47	3.08	2.78	2.14	2.14	2.14	1.74	1.74	1.74	1.46	1.46	1.46	1.07														
//	25.94	12.98	8.65	5.19	4.33	3.71	3.24	2.88	2.6	2	2	2	1.63	1.63	1.63	1.37	1.37	1.37	1	0.72													
//	24.65	12.35	8.23	4.94	4.12	3.53	3.09	2.72	2.45	1.91	1.91	1.91	1.55	1.55	1.55	1.3	1.3	1.3	0.95	0.69	0.54												
//	23.79	11.88	7.92	4.75	3.96	3.39	2.97	2.64	2.37	1.84	1.84	1.84	1.49	1.49	1.49	1.25	1.25	1.25	0.91	0.66	0.52	0.42											
//	22.95	11.51	7.67	4.6	3.84	3.29	2.88	2.56	2.3	1.78	1.78	1.78	1.44	1.44	1.44	1.21	1.21	1.21	0.89	0.64	0.5	0.41	0.35										
//	22.36	11.2	7.47	4.48	3.73	3.2	2.8	2.49	2.25	1.73	1.73	1.73	1.4	1.4	1.4	1.18	1.18	1.18	0.86	0.62	0.49	0.4	0.34	0.3									
//	21.89	10.94	7.3	4.38	3.65	3.13	2.74	2.43	2.2	1.69	1.69	1.69	1.37	1.37	1.37	1.15	1.15	1.15	0.84	0.61	0.48	0.39	0.33	0.29	0.25								
//	21.56	10.72	7.15	4.29	3.57	3.06	2.68	2.38	2.14	1.66	1.66	1.66	1.34	1.34	1.34	1.13	1.13	1.13	0.82	0.6	0.47	0.38	0.32	0.28	0.24	0.23							
//	21.06	10.53	7.02	4.21	3.51	3.01	2.63	2.34	2.1	1.63	1.63	1.63	1.32	1.32	1.32	1.11	1.11	1.11	0.81	0.59	0.46	0.38	0.32	0.27	0.24	0.22	0.2						
//	20.65	10.36	6.91	4.15	3.45	2.96	2.59	2.3	2.08	1.6	1.6	1.6	1.3	1.3	1.3	1.09	1.09	1.09	0.8	0.58	0.45	0.37	0.31	0.27	0.24	0.22	0.2	0.18					
//	20.45	10.21	6.81	4.08	3.4	2.92	2.55	2.27	2.04	1.58	1.58	1.58	1.28	1.28	1.28	1.07	1.07	1.07	0.79	0.57	0.45	0.37	0.3	0.26	0.23	0.21	0.19	0.18	0.17				
//	20.21	10.08	6.72	4.03	3.36	2.88	2.52	2.24	2.03	1.56	1.56	1.56	1.26	1.26	1.26	1.06	1.06	1.06	0.78	0.56	0.44	0.36	0.3	0.26	0.23	0.21	0.19	0.17	0.16	0.15			
//	19.89	9.95	6.63	3.98	3.32	2.84	2.49	2.21	2.01	1.54	1.54	1.54	1.24	1.24	1.24	1.05	1.05	1.05	0.77	0.55	0.43	0.36	0.3	0.26	0.23	0.2	0.19	0.17	0.16	0.15	0.14		
//	19.76	9.84	6.56	3.94	3.28	2.81	2.46	2.19	1.97	1.52	1.52	1.52	1.23	1.23	1.23	1.04	1.04	1.04	0.76	0.55	0.43	0.35	0.29	0.25	0.22	0.2	0.18	0.17	0.16	0.15	0.14	0.13	
//	19.7	9.78	6.48	3.91	3.24	2.78	2.45	2.16	1.95	1.51	1.51	1.51	1.22	1.22	1.22	1.03	1.03	1.03	0.75	0.55	0.43	0.35	0.29	0.25	0.22	0.2	0.18	0.16	0.15	0.14	0.13	0.12	0.11

	
	public static String [][] _sng_blind = {
		{"10/20",""},
		{"15/30",""},
		{"20/40",""},
		{"25/50",""},
		{"30/60",""},
		{"40/80",""},
		{"50/100",""},
		{"60/120",""},
		{"75/150",""},
		{"100/200","15"},
		{"125/250","15"},
		{"150/300","25"},
		{"200/400","25"},
		{"250/500","50"},
		{"300/600","75"},
		{"400/800","75"},
		{"500/1000","100"},
		{"600/1200","150"},
		{"750/1500","150"},
		{"1000/2000","200"},
		{"1250/2500","250"},
		{"1500/3000","300"},
		{"2000/4000","400"},
		{"2500/5000","500"},
		{"3000/6000","600"},
		{"4000/8000","800"},
		{"5000/10000","1000"},
		{"6000/12000","1200"},
		{"7500/15000","1500"},
		{"10000/20000","2000"},
		{"12500/25000","2500"},
		{"15000/30000","3000"},
		{"20000/40000","4000"},
	  };
	
	public static String [][] _mtt_blind = {
		{"10/20",""},
		{"15/30",""},
		{"20/40",""},
		{"25/50",""},
		{"30/60",""},
		{"40/80",""},
		{"50/100",""},
		{"60/120",""},
		{"75/150",""},
		{"100/200","15"},
		{"125/250","15"},
		{"150/300","25"},
		{"200/400","25"},
		{"250/500","50"},
		{"300/600","75"},
		{"400/800","75"},
		{"500/1000","100"},
		{"600/1200","150"},
		{"750/1500","150"},
		{"1000/2000","200"},
		{"1250/2500","250"},
		{"1500/3000","300"},
		{"2000/4000","400"},
		{"2500/5000","500"},
		{"3000/6000","600"},
		{"4000/8000","800"},
		{"5000/10000","1000"},
		{"6000/12000","1200"},
		{"7500/15000","1500"},
		{"10000/20000","2000"},
		{"12500/25000","2500"},
		{"15000/30000","3000"},
		{"20000/40000","4000"},
		{"25000/50000","5000"},
		{"30000/60000","6000"},
		{"40000/80000","8K"},
		{"50K/100K","10K"},
		{"60K/120K","12K"},
		{"75K/150K","15K"},
		{"100K/200K","20K"},
		{"125K/250K","25K"},
		{"150K/300K","30K"},
		{"200K/400K","40K"},
		{"250K/500K","50K"},
		{"300K/600K","60K"},
		{"400K/800K","80K"},
		{"500K/1M","100K"},
		{"600K/1.2M","120K"},
		{"750K/1.5M","150K"},
	  };
	
}
