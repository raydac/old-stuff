package com.igormaznitsa.GameKit_FE652.PackRace;

public class Labyrints
{
    public static final int ELE_EMPTY = 0;
    public static final int ELE_WALL = 1;
    public static final int ELE_RICE = 2;
    public static final int ELE_CREATURE = 3;
    public static final int ELE_HUNTER = 4;
    public static final int ELE_GUN = 5;
    public static final int LAB_NUMBER = 8;
    public static final int FIELD_WIDTH = 20;
    public static final int FIELD_HEIGHT = 20;

    public static final int [] indexes = {
              400, 1638800, 3277200, 4915600, 6554000, 8192400, 9830800, 11469200
};

 private static final long [] storage = {
1229782938247303441l, 2459847351474388992l, 1230064481657165090l, 2459246871824113937l, 19937810850338l, 1302122007531430177l, 2459564772671160320l, 2387225703100715538l, 2387189277786247441l, 19869091385633l, 1302703859686318369l, 1229782938516783104l, 2459564772115354130l, 2387189277786251810l, 19868805042449l, 1522817154001543761l, 1229782938247299072l, 4369l, 0l, 0l, 0l, 0l, 0l, 0l, 0l,
1229782938247303441l, 2459565876497682705l, 1301840601072935458l, 5845973655814742290l, 1302985192877724194l, 1229800534728319265l, 2382967501280580114l, 2450276098187072768l, 2387209146272457250l, 1302122075963593233l, 9288674535678497l, 306264570471715362l, 1297355693126389793l, 2387209214991864081l, 1302120972425363969l, 1162529183809741089l, 2382967501280580114l, 1229800599169668352l, 2387209146288120082l, 1302140767947592226l, 1234304134372479265l, 2382987017343537685l, 2459566078138847521l, 1229784111059706403l, 1229782938247303441l,
4803839602528529l, 2459565807778201600l, 1302122075695087635l, 1234267772877414690l, 77865292306l, 5102979797623361l, 2387208042482565120l, 2459565871626977298l, 1234267772877414690l, 77865226513l, 1230082091595735585l, 5845990217207582993l, 2387189277787300386l, 19933229551905l, 1306644372676223265l, 1229800599441178624l, 2387225703100715537l, 2387189277787300433l, 19864511127842l, 1306696044622455073l, 2387226803168149504l, 2459284396650271249l, 2472757670706225441l, 23236882604580l, 1229782938247303441l,
1229782938247303441l, 2459565876495585553l, 2382704511591522850l, 2387212513559253265l, 1229784042340229410l, 2455043512155119905l, 1522816067374748194l, 2387507178380529938l, 1234287710687072785l, 1306344131929448721l, 2459565808312001057l, 1230065586036347426l, 1229783007018160401l, 2459266809331851810l, 1301840532306272802l, 2459548211007787297l, 1302140772243542562l, 2460409197348589842l, 2387209146623726114l, 1302122011573768738l, 2459548215589085729l, 1301840532286280210l, 2460128821883506961l, 1229787409594589730l, 1229782938247303441l,
73300775185l, 5106278600867840l, 1229783006680449024l, 5845953791303614482l, 1229782938247373345l, 2459565876477829409l, 1229782938516787746l, 2459564772418458129l, 1234287714712756770l, 1301841636091826449l, 1302126478642782753l, 1301840532285231653l, 303109394l, 19864510140944l, 1306644303167094784l, 1306626701819641856l, 1301540292310077713l, 304226834l, 19864510140944l, 1306679555990224896l, 1230909933084475392l, 1301540292310077969l, 354558481l, 18765016343056l, 1229782938247233536l,
1229781765721231633l, 1152941442418675985l, 1301840601056158242l, 5845973729097617681l, 1301840605589152274l, 5845972556319957281l, 1152941369117970977l, 1306644367324291362l, 2382704516154855424l, 76879025540174097l, 2377900603251695872l, 4848615425l, 4821427207348480l, 2454744444974465024l, 1234586772982202642l, 1234267772895236641l, 1229783006967829025l, 2459336074000945441l, 2387209142262702674l, 1302139672462430498l, 1234285365081219361l, 1241818141202l, 81667467701785121l, 1229786310081839104l, 1229782933666005265l,
1229782938247303441l, 2459565876496634129l, 1230064482011718178l, 4693052151206842641l, 1302985197190521378l, 1229784037758935329l, 2459566009621746194l, 1234304198797038882l, 2387209146304893201l, 1302123192959254817l, 1302122076250906913l, 1306361793087607314l, 2387225703403885074l, 2387209146304959025l, 1302123111085318433l, 1302985197995761953l, 1229783007236264466l, 2472794096323924497l, 2387209146356343330l, 1302122007262990609l, 2459565876494618913l, 1229782938248352277l, 2459566078088519953l, 1229787409594589730l, 1229782938247303441l,
1229764173249974545l, 18769580724497l, 1522798457774023201l, 2387209141997338624l, 1302984024091791906l, 2454461870234477089l, 1230083173926375954l, 1234589998805750289l, 1229502635814491426l, 1518313554374172961l, 1230064413510270976l, 2472795126812905745l, 2670634579030704146l, 18764998512929l, 1306644372407787792l, 2387226807462007057l, 2382723134572794386l, 2459565871930151457l, 2387209146557604097l, 1302123175760437521l, 2472793959137681697l, 2378218435413873173l, 5918330523189580049l, 1229787409594589441l, 1229782938246254865l,
};

	 public final static int [] getLabyrinth(int id)
	 {
	   id=indexes[id];
	   try {
 	    int[] img = new int[id&0xffff];
	    id>>>=16;
	    int pos = 0;
	    long n=0;
	    while (pos < img.length) {
	      if ((pos&15)==0)
	      /* if (pos == 0) n = storage[0];
	         else */ n=storage[id+(pos>>4)];
	      img[pos++]=(int)(n&15);
	      n>>>=4;
	    }
               return img;
	   } catch (Exception e) {return null;}
	 }



}