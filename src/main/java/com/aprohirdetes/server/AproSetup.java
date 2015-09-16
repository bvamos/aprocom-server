package com.aprohirdetes.server;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.utils.MongoUtils;

public class AproSetup {

	public static void main(String[] args) {
		Datastore ds = new Morphia().createDatastore(MongoUtils.getMongo(), "aprocom");
		ds.delete(ds.createQuery(Kategoria.class));

		ds.save(new Kategoria("52d3e4fcd1f386a6190b7a17","Ingatlan","ingatlan",1,null));
		
			ds.save(new Kategoria("52d9a36f44aee9ff29a64678","Lakás","lakas",1,"52d3e4fcd1f386a6190b7a17"));
			ds.save(new Kategoria("52d9ad5244aee655a135e66a","Ház","haz",2,"52d3e4fcd1f386a6190b7a17"));
			ds.save(new Kategoria("53e0c9a3841cabaf0d7cbfc6","Építési telek","epitesi-telek",3,"52d3e4fcd1f386a6190b7a17"));
			ds.save(new Kategoria("52d9ad8f44aee655a135e66b","Albérlet","alberlet",4,"52d3e4fcd1f386a6190b7a17"));
			ds.save(new Kategoria("52d9ae4f44aee655a135e66c","Iroda, üzlethelyiség","iroda-uzlethelyiseg",5,"52d3e4fcd1f386a6190b7a17"));
			ds.save(new Kategoria("52d9ae6f44aee655a135e66d","Egyéb bérlemény","egyeb-berlemeny",6,"52d3e4fcd1f386a6190b7a17"));
			ds.save(new Kategoria("52d9ae8844aee655a135e66e","Nyaraló","nyaralo",7,"52d3e4fcd1f386a6190b7a17"));
			ds.save(new Kategoria("52d9ae9844aee655a135e66f","Garázs","garazs",8,"52d3e4fcd1f386a6190b7a17"));
			ds.save(new Kategoria("52d9aec744aee655a135e670","Szántó, kiskert","szanto-kiskert",9,"52d3e4fcd1f386a6190b7a17"));
			ds.save(new Kategoria("52d9aee044aee655a135e671","Szolgáltatás","ingatlan-szolgaltatas",10,"52d3e4fcd1f386a6190b7a17"));

		ds.save(new Kategoria("52d9ac3544aee655a135e664","Jármű","jarmu",2,null));
		
			ds.save(new Kategoria("52daadbc44aee655a135e68a","Személyautó","szemelyauto",1,"52d9ac3544aee655a135e664"));
			ds.save(new Kategoria("52daadf544aee655a135e68b","Kishaszon","kishaszon",2,"52d9ac3544aee655a135e664"));
			ds.save(new Kategoria("52daae0844aee655a135e68c","Haszonjármű","haszonjarmu",3,"52d9ac3544aee655a135e664"));
			ds.save(new Kategoria("52daae8044aee655a135e691","Alkatrész","alkatresz",4,"52d9ac3544aee655a135e664"));
			ds.save(new Kategoria("52daae9444aee655a135e692","Gumi, felni","gumi-felni",5,"52d9ac3544aee655a135e664"));
			ds.save(new Kategoria("52daae2844aee655a135e68d","Pótkocsi, utánfutó","potkocsi-utanfuto",6,"52d9ac3544aee655a135e664"));
			ds.save(new Kategoria("52daae4144aee655a135e68e","Motor","motor",7,"52d9ac3544aee655a135e664"));
			ds.save(new Kategoria("52daae4e44aee655a135e68f","Busz","busz",8,"52d9ac3544aee655a135e664"));
			ds.save(new Kategoria("52daae6044aee655a135e690","Lakókocsi","lakokocsi",9,"52d9ac3544aee655a135e664"));
			ds.save(new Kategoria("52daaf8844aee655a135e693","Hajó","hajo",10,"52d9ac3544aee655a135e664"));
		
		ds.save(new Kategoria("52d9abe944aee655a135e661","Állás, munka","allas",3,null));
		
			ds.save(new Kategoria("52d9b1e644aee655a135e679","Ipari","allas-ipari",1,"52d9abe944aee655a135e661"));
			ds.save(new Kategoria("52d9b21744aee655a135e67a","Kereskedelmi","kereskedelmi",2,"52d9abe944aee655a135e661"));
			ds.save(new Kategoria("52d9b22b44aee655a135e67b","Szellemi","szellemi",3,"52d9abe944aee655a135e661"));
			ds.save(new Kategoria("52d9b24744aee655a135e67c","Távmunka","tavmunka",4,"52d9abe944aee655a135e661"));
			ds.save(new Kategoria("52d9b25644aee655a135e67d","Diákmunka","diakmunka",5,"52d9abe944aee655a135e661"));
			ds.save(new Kategoria("52d9b26944aee655a135e67e","Egyéb","allas-egyeb",6,"52d9abe944aee655a135e661"));
			ds.save(new Kategoria("52d9b28644aee655a135e67f","Tanítás","tanitas",7,"52d9abe944aee655a135e661"));
			ds.save(new Kategoria("52d9b29a44aee655a135e680","Szolgáltatás","allas-szolgaltatas",8,"52d9abe944aee655a135e661"));
			ds.save(new Kategoria("52d9b2ae44aee655a135e681","Vállalkozás","vallalkozas",9,"52d9abe944aee655a135e661"));
		
		ds.save(new Kategoria("52d9a6c844aee655a135e660","Bazár","bazar",4,null));
		
			ds.save(new Kategoria("52d9af6344aee655a135e672","Vegyes","vegyes",1,"52d9a6c844aee655a135e660"));
			ds.save(new Kategoria("52d9afd444aee655a135e675","Építőanyag","epitoanyag",2,"52d9a6c844aee655a135e660"));
			ds.save(new Kategoria("52d9b00744aee655a135e677","Ingyen elvihető","ingyen-elviheto",3,"52d9a6c844aee655a135e660"));
			ds.save(new Kategoria("52d9acac44aee655a135e669","Események","esemeny",4,"52d9a6c844aee655a135e660"));
		
				ds.save(new Kategoria("52dab73144aee655a135e6a3","Adó 1%","ado-1-szazaleka",1,"52d9acac44aee655a135e669"));
				ds.save(new Kategoria("52dab74c44aee655a135e6a4","Valentin nap","valentin-nap",2,"52d9acac44aee655a135e669"));
				ds.save(new Kategoria("52dab75d44aee655a135e6a5","Farsang","farsang",3,"52d9acac44aee655a135e669"));
				ds.save(new Kategoria("52dab76e44aee655a135e6a6","Húsvét","husvet",4,"52d9acac44aee655a135e669"));
				ds.save(new Kategoria("52dab77e44aee655a135e6a7","Karácsony","karacsony",5,"52d9acac44aee655a135e669"));
				ds.save(new Kategoria("52dab79444aee655a135e6a8","Szilveszter","szilveszter",6,"52d9acac44aee655a135e669"));
		
		ds.save(new Kategoria("55d1890e52194c31f74808d1","Otthon, háztartás","otthon-haztartas",5,null));
			ds.save(new Kategoria("52d9af7a44aee655a135e673","Lakberendezés","lakberendezes",1,"55d1890e52194c31f74808d1"));
				ds.save(new Kategoria("55f7d2a8116fe91b2a464287","Bútor","butor",1,"52d9af7a44aee655a135e673"));
				ds.save(new Kategoria("55f7d2b3116fe91b2a464288","Világítás","vilagitas",2,"52d9af7a44aee655a135e673"));
				ds.save(new Kategoria("55f7d2c2116fe91b2a46428a","Lakáskiegészítők","lakaskiegeszitok",3,"52d9af7a44aee655a135e673"));
				ds.save(new Kategoria("55f7d2ca116fe91b2a46428c","Konyhai felszerelések","konyhai-felszerelesek",4,"52d9af7a44aee655a135e673"));
				ds.save(new Kategoria("55f7d2d0116fe91b2a46428f","Fürdőszoba","furdoszoba",5,"52d9af7a44aee655a135e673"));
				ds.save(new Kategoria("55f7d2d7116fe91b2a464292","Kerti bútor","kerti-butor",6,"52d9af7a44aee655a135e673"));
			ds.save(new Kategoria("53df2934717f0f5b4cb2c64f","Divat, ruházat","divat",2,"55d1890e52194c31f74808d1"));
				ds.save(new Kategoria("55d1898a52194c31f74808d3","Ruha, kiegeszítő","ruha",1,"53df2934717f0f5b4cb2c64f"));
				ds.save(new Kategoria("55d1898a52194c31f74808d4","Karóra","karora",2,"53df2934717f0f5b4cb2c64f"));
				ds.save(new Kategoria("55d1898a52194c31f74808d5","Ékszer","ekszer",3,"53df2934717f0f5b4cb2c64f"));
			ds.save(new Kategoria("52d9afa844aee655a135e674","Könyv, CD, DVD","konyv-cd-dvd",3,"55d1890e52194c31f74808d1"));
			ds.save(new Kategoria("52d9afec44aee655a135e676","Műtárgy","mutargy",4,"55d1890e52194c31f74808d1"));
			
		ds.save(new Kategoria("52d9b03a44aee655a135e678","Baba, mama","baba-mama",6,null));
			ds.save(new Kategoria("533dae4821ad39681203cbf5","Babakocsi","babakocsi",1,"52d9b03a44aee655a135e678"));
			ds.save(new Kategoria("533daf0021ad39681203cbf6","Bababútor, kellék","bababutor",2,"52d9b03a44aee655a135e678"));
			// TODO: Ketszer van babakocsi
			ds.save(new Kategoria("533daf3921ad39681203cbf7","Babakocsi, hordozó","babakocsi-hordozo",3,"52d9b03a44aee655a135e678"));
			ds.save(new Kategoria("533daf6c21ad39681203cbf8","Babaruha","babaruha",4,"52d9b03a44aee655a135e678"));
			ds.save(new Kategoria("533daf8321ad39681203cbf9","Kismama ruha","kismamaruha",5,"52d9b03a44aee655a135e678"));
			ds.save(new Kategoria("533dafb521ad39681203cbfa","Játék","gyerekjatek",6,"52d9b03a44aee655a135e678"));
			ds.save(new Kategoria("533dafcd21ad39681203cbfb","Egyéb","baba-mama-egyeb",7,"52d9b03a44aee655a135e678"));
			
		ds.save(new Kategoria("52d9ac9744aee655a135e668","Szabadidő, sport","szabadido-sport",7,null));
			ds.save(new Kategoria("52dab69a44aee655a135e69e","Fényképezés","fenykepezes",1,"52d9ac9744aee655a135e668"));
			ds.save(new Kategoria("52dab6ae44aee655a135e69f","Sportszer","sportszer",2,"52d9ac9744aee655a135e668"));
			ds.save(new Kategoria("52dab6bc44aee655a135e6a0","Hangszer","hangszer",3,"52d9ac9744aee655a135e668"));
				ds.save(new Kategoria("55f7d4da116fe91b2a464296","Húros","hangszer-huros",1,"52dab6bc44aee655a135e6a0"));
				ds.save(new Kategoria("55f7d4e2116fe91b2a464298","Billentyűs","hangszer-billentyus",2,"52dab6bc44aee655a135e6a0"));
				ds.save(new Kategoria("55f7d4e9116fe91b2a464299","Ütős","hangszer-utos",3,"52dab6bc44aee655a135e6a0"));
				ds.save(new Kategoria("55f7d4ee116fe91b2a46429a","Fuvós","hangszer-fuvos",4,"52dab6bc44aee655a135e6a0"));
				ds.save(new Kategoria("55f7d4f3116fe91b2a46429b","Egyéb hangszer","hangszer-egyeb",5,"52dab6bc44aee655a135e6a0"));
				ds.save(new Kategoria("55f7d4f8116fe91b2a46429c","Kiegészítők","hangszer-kiegeszitok",6,"52dab6bc44aee655a135e6a0"));
			ds.save(new Kategoria("52dab6d544aee655a135e6a1","Nyaralás, utazas","nyaralas-utazas",4,"52d9ac9744aee655a135e668"));
			ds.save(new Kategoria("52dab6eb44aee655a135e6a2","Életmód","eletmod",5,"52d9ac9744aee655a135e668"));
			ds.save(new Kategoria("53766eac705cef347fff5293","Kerékpár","kerekpar",6,"52d9ac9744aee655a135e668"));
			ds.save(new Kategoria("55f7d3a2116fe91b2a464294","Játék","jatek",7,"52d9ac9744aee655a135e668"));
		
		ds.save(new Kategoria("52d9ac0e44aee655a135e662","Számítástechnika","szamitastechnika",8,null));
			ds.save(new Kategoria("55f8039c116fe91b2a46429e","Asztali PC","asztali-pc",1,"52d9ac0e44aee655a135e662"));
			ds.save(new Kategoria("55f803a2116fe91b2a46429f","Laptop","laptop",2,"52d9ac0e44aee655a135e662"));
			ds.save(new Kategoria("52daaa8e44aee655a135e683","Szoftver","szoftver",3,"52d9ac0e44aee655a135e662"));
			ds.save(new Kategoria("52daaabc44aee655a135e684","Hardver","hardver",4,"52d9ac0e44aee655a135e662"));
				ds.save(new Kategoria("55f803a7116fe91b2a4642a0","Videókártya","videokartya",1,"52daaabc44aee655a135e684"));
				ds.save(new Kategoria("55f803af116fe91b2a4642a1","Memória","memoria",2,"52daaabc44aee655a135e684"));
				ds.save(new Kategoria("55f803b4116fe91b2a4642a2","Processzor, alaplap","cpu-alaplap",3,"52daaabc44aee655a135e684"));
				ds.save(new Kategoria("55f803bb116fe91b2a4642a4","Adattároló, merevlemez","hdd",4,"52daaabc44aee655a135e684"));
				ds.save(new Kategoria("55f803c0116fe91b2a4642a6","Monitor","monitor",5,"52daaabc44aee655a135e684"));
				ds.save(new Kategoria("55f803c5116fe91b2a4642a7","Hálózati eszköz","halozati-eszkoz",6,"52daaabc44aee655a135e684"));
			ds.save(new Kategoria("52daaa3744aee655a135e682","Egyéb alkatrész","szamitastechnika-alkatresz",5,"52d9ac0e44aee655a135e662"));
			ds.save(new Kategoria("52daaad144aee655a135e685","Szolgáltatás","szamitastechnika-szolgaltatas",6,"52d9ac0e44aee655a135e662"));
			ds.save(new Kategoria("52daaae444aee655a135e686","Internet","internet",7,"52d9ac0e44aee655a135e662"));
			
		ds.save(new Kategoria("52d9ac4a44aee655a135e665","Elektronikai cikk","elekronika",9,null));
			ds.save(new Kategoria("52dab02c44aee655a135e694","Audio","audio",1,"52d9ac4a44aee655a135e665"));
				ds.save(new Kategoria("55f805cf116fe91b2a4642a8","CD, MP3 lejátszó","",1,"52dab02c44aee655a135e694"));
				ds.save(new Kategoria("55f805d4116fe91b2a4642a9","Hifi, erősítő","",2,"52dab02c44aee655a135e694"));
				ds.save(new Kategoria("55f805d8116fe91b2a4642aa","Rádió","",3,"52dab02c44aee655a135e694"));
			ds.save(new Kategoria("52dab04244aee655a135e695","Video","video",2,"52d9ac4a44aee655a135e665"));
				ds.save(new Kategoria("55f805df116fe91b2a4642ac","DVD, házimozi","",1,"52dab04244aee655a135e695"));
			ds.save(new Kategoria("55f805e6116fe91b2a4642ad","TV","tv",3,"52d9ac4a44aee655a135e665"));
			ds.save(new Kategoria("52dab05b44aee655a135e696","Telefon","telefon",4,"52d9ac4a44aee655a135e665"));
			ds.save(new Kategoria("52dab08044aee655a135e697","Tablet","tablet",5,"52d9ac4a44aee655a135e665"));
			ds.save(new Kategoria("5373c0378e7bd915aa874b25","Játékkonzol","jatekkonzol",6,"52d9ac4a44aee655a135e665"));
			
		ds.save(new Kategoria("52d9ac2444aee655a135e663","Gépek","gepek",10,null));
			ds.save(new Kategoria("52daab4644aee655a135e687","Háztartási kisgép","haztartasi-kisgep",1,"52d9ac2444aee655a135e663"));
				ds.save(new Kategoria("55f82e7b116fe91b2a4642b7","Konyhai gép","konyhai-gep",1,"52daab4644aee655a135e687"));
				ds.save(new Kategoria("55f82d54116fe91b2a4642b3","Porszívó","porszivo",2,"52daab4644aee655a135e687"));
				//ds.save(new Kategoria("","Turmixgép","turmixgep",1,"52daab4644aee655a135e687"));
			ds.save(new Kategoria("55f82c4f116fe91b2a4642b2","Háztartási nagygép","haztartasi-nagygep",2,"52d9ac2444aee655a135e663"));
				ds.save(new Kategoria("55f82df1116fe91b2a4642b4","Mosógép, szárítógép","mosogep-szaritogep",1,"55f82c4f116fe91b2a4642b2"));
				ds.save(new Kategoria("55f82df8116fe91b2a4642b5","Hűtőgép","hutogep",2,"55f82c4f116fe91b2a4642b2"));
				ds.save(new Kategoria("55f82e01116fe91b2a4642b6","Tűzhely, sütő","tuzhely-suto",3,"55f82c4f116fe91b2a4642b2"));
			ds.save(new Kategoria("55f82ee4116fe91b2a4642b8","Irodai gép","irodai-gep",3,"52d9ac2444aee655a135e663"));
			ds.save(new Kategoria("52daab6244aee655a135e688","Munkagép","munkagep",4,"52d9ac2444aee655a135e663"));
			ds.save(new Kategoria("52daab8744aee655a135e689","Szerszám","szerszam",5,"52d9ac2444aee655a135e663"));
			
		ds.save(new Kategoria("52d9ac6844aee655a135e666","Állat, Növény","allat-noveny",11,null));
			ds.save(new Kategoria("52dab50e44aee655a135e698","Háziállat","haziallat",1,"52d9ac6844aee655a135e666"));
				ds.save(new Kategoria("55f80664116fe91b2a4642ae","Kutya","haziallat-kutya",1,"52dab50e44aee655a135e698"));
				ds.save(new Kategoria("55f80669116fe91b2a4642af","Macska","haziallat-macska",2,"52dab50e44aee655a135e698"));
				ds.save(new Kategoria("55f8066e116fe91b2a4642b1","Egyéb háziállat","haziallat-egyeb",3,"52dab50e44aee655a135e698"));
			ds.save(new Kategoria("52dab52144aee655a135e699","Haszonállat","haszonallat",2,"52d9ac6844aee655a135e666"));
			ds.save(new Kategoria("52dab53744aee655a135e69a","Növény","noveny",3,"52d9ac6844aee655a135e666"));
			
		ds.save(new Kategoria("52d9ac7844aee655a135e667","Oktatás","oktatas",12,null));
			ds.save(new Kategoria("52dab5f144aee655a135e69b","Tanfolyam","tanfolyam",1,"52d9ac7844aee655a135e667"));
			ds.save(new Kategoria("52dab60644aee655a135e69c","Korrepetálás","korrepetalas",2,"52d9ac7844aee655a135e667"));
			ds.save(new Kategoria("52dab61944aee655a135e69d","Egyéb","oktatas-egyeb",3,"52d9ac7844aee655a135e667"));
			
	}

}
