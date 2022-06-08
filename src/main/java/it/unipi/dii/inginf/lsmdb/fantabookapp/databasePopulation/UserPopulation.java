package it.unipi.dii.inginf.lsmdb.fantabookapp.databasePopulation;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.MiddlewareConnector;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao.*;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Squad;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.User;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection.Collections;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection.MongoDriver;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.neo4jconnection.Neo4jDriver;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.exceptions.NoSuchRecordException;

import java.time.Year;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sample;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static org.neo4j.driver.Values.parameters;

public class UserPopulation {

    private static final MiddlewareConnector connector = MiddlewareConnector.getInstance();


    public static void main(String[] args) throws ActionNotCompletedException {
        populateWithUser(100000);
    }
    private static void populateWithUser(int howManyUsers) throws ActionNotCompletedException {
        Random generator = new Random();

        //String[] firstName =  new String[] {"Emily","Hannah","Madison","Ashley","Sarah","Alexis","Samantha","Jessica","Elizabeth","Taylor","Lauren","Alyssa","Kayla","Abigail","Brianna","Olivia","Emma","Megan","Grace","Victoria","Rachel","Anna","Sydney","Destiny","Morgan","Jennifer","Jasmine","Haley","Julia","Kaitlyn","Nicole","Amanda","Katherine","Natalie","Hailey","Alexandra","Adam", "Alex", "Aaron", "Ben", "Carl", "Dan", "David", "Edward", "Fred", "Frank", "George", "Hal", "Hank", "Ike", "John", "Jack", "Joe", "Larry", "Monte", "Matthew", "Mark", "Nathan", "Otto", "Paul", "Peter", "Roger", "Roger", "Steve", "Thomas", "Tim", "Ty", "Victor", "Walter", "Alessio", "Valerio", "Lorenzo", "Giacomo", "Marco", "Mario", "Salvatore"};

        //String[] firstName = new String[] {"Aaden","Aaliyah","Aarav","Aaron","Ab","Abagail","Abb","Abbey","Abbie","Abbigail","Abbott","Abby","Abdiel","Abdul","Abdullah","Abe","Abel","Abelardo","Abie","Abigail","Abigale","Abigayle","Abner","Abraham","Abram","Abril","Ace","Acey","Achsah","Acie","Acy","Ada","Adah","Adalberto","Adaline","Adalyn","Adalynn","Adam","Adamaris","Adams","Adan","Add","Adda","Addie","Addison","Addisyn","Addyson","Adel","Adela","Adelaide","Adelard","Adelbert","Adele","Adelia","Adelina","Adeline","Adell","Adella","Adelle","Adelyn","Aden","Adilene","Adin","Adina","Adison","Aditya","Adlai","Adline","Admiral","Adolf","Adolfo","Adolph","Adolphus","Adonis","Adrain","Adria","Adrian","Adriana","Adriane","Adrianna","Adrianne","Adriel","Adrien","Adriene","Adrienne","Adron","Adyson","Aedan","Affie","Afton","Agatha","Aggie","Agnes","Agness","Agusta","Agustin","Agustus","Ah","Ahmad","Ahmed","Aida","Aidan","Aiden","Aidyn","Aileen","Ailene","Aili","Aimee","Ainsley","Aisha","Aiyana","Aja","Akeelah","Akeem","Akira","Al","Ala","Alabama","Alaina","Alan","Alana","Alani","Alanna","Alannah","Alanzo","Alayna","Alba","Albert","Alberta","Albertha","Albertina","Albertine","Alberto","Albertus","Albin","Albina","Albion","Alby","Alcee","Alcide","Alcie","Alda","Alden","Aldo","Aldona","Aleah","Alease","Alec","Alecia","Aleck","Aleen","Aleena","Alejandra","Alejandro","Alek","Alena","Alene","Alesha","Alesia","Alessandra","Alessandro","Aleta","Aletha","Alethea","Alex","Alexa","Alexande","Alexander","Alexandr","Alexandra","Alexandre","Alexandrea","Alexandria","Alexandro","Alexia","Alexina","Alexis","Alexus","Alexys","Alexzander","Alf","Alferd","Alfie","Alfonse","Alfonso","Alfonzo","Alford","Alfred","Alfreda","Alfredo","Alger","Algernon","Algie","Algot","Ali","Alia","Aliana","Alice","Alicia","Alida","Alijah","Alina","Aline","Alisa","Alisha","Alison","Alissa","Alisson","Alivia","Aliya","Aliyah","Aliza","Alize","Alla","Allan","Allean","Alleen","Allen","Allena","Allene","Allie","Alline","Allison","Allisson","Ally","Allyn","Allyson","Allyssa","Alma","Almeda","Almedia","Almer","Almeta","Almina","Almira","Almon","Almond","Almus","Almyra","Alois","Aloma","Alondra","Alonso","Alonza","Alonzo","Aloys","Aloysius","Alpha","Alpheus","Alphons","Alphonse","Alphonsine","Alphonso","Alphonsus","Alston","Alta","Altha","Althea","Altie","Alto","Alton","Alva","Alvah","Alvan","Alvaro","Madora","Mable","Macel","Magen","Magan","Maddox","Madden","Mafalda","Maddison","Madison","Malik","Madeline","Madelynn","Makaila","Madalyn","Magdalena","Maci","Madge","Madelyn","Mahala","Maebelle","Maegan","Madisen","Major","Mandi","Malakai","Madilyn","Makai","Macarthur","Makenzie","Magdalene","Manda","Malachi","Makenna","Madie","Mallie","Maia","Malinda","Makala","Macy","Mabel","Malvina","Madeleine","Maeve","Mahalia","Maceo","Magdalen","Madalynn","Malissa","Mac","Maida","Maleah","Mamie","Madelene","Makena","Macie","Mabelle","Madonna","Madaline","Madisyn","Mahlon","Makayla","Madilynn","malesie","Mame","Macey","Maggie","Magnolia","Mace","Malorie","Malvin","Malaki","Machelle","Mai","Mabell","Makhi","Madlyn","Madyson","Malcom","maled","Mack","Manerva","Malcolm","Mandy","Maebell","Malaya","Mal","Male","Maiya","Mae","Mallorie","Mackenzie","malece","Mallory","Mahalie","Malissie","Maira","Mandie","Malia","Maliyah","Mammie","Manervia","Manuela","Manuelita","Mannie","Manley","Manila","Manilla","Mansfield","Manford","Manie","Marc","Manning","Manuel","Manly","Mara","Manson","Maranda","Marceline","Marco","Marcelino","Marcela","Marcella","Marcelina","Marcelo","Maralyn","Marcel","Marcelle","Marcello","Marcellus","Marcia","Marcy","Marci","Mareli","Marely","Marchello","Margaretha","Marcus","Marcie","Mardell","Marcos","Maren","Margarite","Margaretta","Maria","Margarette","Margarete","Marianna","Margarita","Mariam","Margery","Mari","Margaret","Marget","Margarito","Margarett","Mariann","Margeret","Margrett","Marilla","Margene","Margo","Margret","Marguerite","Margretta","Maribel","Mariano","Marge","Marianne","Margurite","Marian","Mariah","Margot","Marietta","Margy","Maricela","Margie","Mariela","Margueritta","Marie","Marianita","Mariana","Maribeth","Mariel","Marilee","Marilyn","Marilou","Marinda","Marin","Marina","Mario","Marilynn","Maritza","Marita","Marisol","Marisa","Marion","Marisela","Marius","Mariyah","Marissa","Marjorie","Mark","Marjory","Markell","Marla","Markita","Marland","Marlana","Markel","Marlene","Markus","Marlen","Marleen","Marlena","Marlo","Marlon","Marlee","Marlin","Marley","Marni","Martell","Marty","Marlie","Marnie","Marquez","Marquita","Marquis","Marta","Marques","Marquise","Marry","Marlys","Marnita","Marlyn","Marvel","Marsh","Marshal","Mart","Marolyn","Marva","Martez","Martina","Marsha","Marshall","Marti","Marvin","Martine","Marrion","Maryann","Martin","Martika","Maryanne","Martha","Maryam","Maryellen","Marylou","Maryjo","Marybelle","Maryjane","Mary","Marylouise","Masao","Matie","Marybeth","Mathews","Masako","Matilda","Marylin","Mason","Mathew","Matthew","Marylee","Mat","Mathias","Matthias","Math","Mathilda","Marylyn","Mathilde","Matilde","Maude","Mateo","Matias","Max","Matt","Matteo","Mauricio","Mattie","Maxie","Mattye","Maudie","Maurice","Maura","Maury","Maud","Maurine","Mauro","Maverick","Maureen","Mavis","Maximilian","Maya","Mckayla","Maximus","Mayo","Maxwell","Maye","Maximillian","May","Maybell","Maxim","Maxine","Maynard","Maybelle","Maximo","Maymie","Mckinley","Mcarthur","Mayra","Mazie","Mayme","Mckenna","Meadow","Meggan","Megan","Media","Meda","Meghan","Meg","Meagan","Mckenzie","Mechelle","Meaghan","Melanie","Mekhi","Mel","Mearl","Medora","Melisa","Menachem","Melva","Mell","Melbourne","Melba","Melinda","Melonie","Mellissa","Melany","Melodee","Meghann","Mellisa","Mercedes","Melton","Melina","Meredith","Memphis","Melody","Melissa","Melony","Melissia","Melodie","Melvyn","Mellie","Melville","Mercer","Mena","Merlene","Melvina","Melvin","Mercy","Mendy","Merrie","Merle","Merlin","Merl","Merilyn","Merna","Merry","Merri","Merrilee","Merlyn","Merrily","Merwin","Messiah","Mervin","Mia","Meta","Merton","Merritt","Mettie","Mervyn","Meryl","Merrill","Metta","Mertie","Metha","Miah","Meyer","Metro","Micaela","Micah","Michael","Michaela","Michal","Mignon","Mickie","Mikaila","Miesha","Micayla","Michaele","Michelina","Mikaela","Michele","Michale","Migdalia","Michelle","Micky","Miguel","Miguelangel","Michell","Michel","Micheal","Mickey","Michial","Mila","Millard","Mikel","Mildred","Mikhail","Mike","Mikayla","Mikal","Mikalah","Milagros","Milas","Milford","Mikala","Milda","Mikeal","Milan","Milburn","Miles","Miller","Milly","Millie","Mina","Miley","Milissa","Minnie","Miranda","Millicent","Mima","Minna","Mills","Minervia","Minor","Milton","Milo","Miracle","Mindy","Minda","Mira","Minerva","Mireya","Mindi","Minta","Miner","Mimi","Misael","Minoru","Mistie","Mittie","Mirtie","Misti","Mitzi","Missie","Missouri","Moesha","Mintie","Miriam","Mitchell","Miriah","Missy","Misty","Monique","Miya","Moira","Mitch","Mohammad","Modena","Mona","Mohammed","Monika","Moises","Monica","Mitchel","Mollie","Moe","Mohamed","Molly","Monna","Monroe","Montie","Monnie","Mont","Montana","Montgomery","Monserrat","Montel","Moriah","Morris","Mordechai","Mora","Monte","Moshe","Montserrat","Morton","Montrell","Moody","Monty","Mozelle","Mossie","Moses","Mozell","Mortimer","Morgan","Murphy","Mylie","Mozella","Mustafa","Murdock","Mose","Myron","Mykel","Murry","Myrl","Mychal","Muhammad","Murray","Muriel","Murl","Myrle","Myla","Myrtie","Myra","Mya","Myer","Myrna","Myah","Myles","Myrtis","Mylee","Myrta","Myranda","Myrtice","Myrtle","Gabriella","Gasper","Gabe","Gauge","Genie","Gearld","Gerhardt","Gena","Garner","Gennie","Gaither","Gavyn","Garold","Gabriela","Genevieve","Garvin","Garland","Gail","Geoff","Gardner","Gaetano","Gabrielle","Garey","Genevra","Gee","Garnett","Garnet","Garfield","Gaylen","George","Gary","Genesis","Gaylon","Gael","Gerry","Georganna","Georgie","Gayla","Gaylene","Georgiana","General","Garrick","Garry","Garrison","Geneva","Gage","Gaston","Gaven","Georgianna","Gay","Garth","Geraldo","Geralyn","Georgene","Georgeann","Gerhard","Gerri","Garett","Galilea","Gannon","Gale","Georgetta","Georgia","Genaro","Geno","Gavin","Gaye","Gerard","Genoveva","Georgine","Gerald","Gaines","Gaylord","Gaige","Garrett","Geary","Garret","Geovanni","Galen","Georgeanna","Gemma","Gearldine","Georgina","Gabriel","Geraldine","Gayle","Gerrit","Georgette","Gerda","Georgiann","Gennaro","Gerold","Gaynell","Gene","German","Geoffrey","Gerardo","Geo","Gertha","Germaine","Geri","Giana","Giada","Gideon","Gibson","Gilbert","Gillian","Gertrude","Gianna","Gia","Gil","Gianni","Gifford","Gigi","Giovanna","Gertie","Giovani","Gilmore","Gina","Gilda","Giselle","Ginny","Gisele","Gilford","Giles","Gilman","Giancarlo","Gladis","Glendon","Glenda","Giovanny","Gladstone","Gilmer","Giuliana","Glennis","Grace","Gino","Golda","Glynda","Glenn","Gonzalo","Glover","Gorden","Gidget","Glennie","Gladys","Glinda","Gillie","Goebel","Girtha","Gilberto","Governor","Glynn","Goldia","Glen","Grayling","Gorge","Graciela","Giuseppe","Grayson","Greg","Ginger","Giovanni","Grady","Graves","Gregg","Granville","Gisselle","Gloria","Griffith","Graham","Gottlieb","Gregory","Grayce","Godfrey","Gracia","Gladyce","Glenna","Greta","Griffin","Glendora","Glenwood","Goldie","Green","Gregoria","Gracelyn","Gracie","Gordon","Golden","Glynis","Greene","Grant","Graydon","Gretta","Greggory","Greyson","Gregorio","Grafton","Gray","Grecia","Gretchen","Grisel","Griselda","Guadalupe","Gunda","Grove","Guilford","Gus","Grover","Gussie","Guillermo","Gunnar","Guido","Gunner","Gurney","Guss","Gustaf","Gwyneth","Gustave","Gwenda","Gwendolyn","Gustavo","Gustav","Gust","Gusta","Guy","Gwen","Gwyn","Guthrie","Gustavus","Gustie","Daisha","Daisye","Daneen","Daniela","Dalvin","Dana","Daijah","Daniella","Dakotah","Dalia","Damon","Damian","Daniele","Darrin","Daija","Darci","Dallas","Daja","Dameon","Dan","Danae","Dale","Darrell","Dalton","Dania","Dangelo","Dakota","Dagny","Danna","Dani","Damari","Dandre","Dakoda","Darian","Dannielle","Dafne","Danial","Damion","Daisey","Damien","Daisie","Danelle","Danyelle","Danny","Danielle","Danica","Dagmar","Darron","Daria","Danika","Danita","Darcie","Damarcus","Darleen","Daisy","Darien","Danyel","Dann","Darla","Darin","Danyell","Danniel","Darrian","Dario","Daphne","Darion","Darrius","Dane","Damarion","Damond","Damaris","Darryle","Darrick","Darryl","Darcy","Daniel","Dabney","Dannie","Darl","Dahlia","Dallin","Dara","Darlyne","Darrel","Darell","Danette","Daron","Daquan","Dante","Darrien","Darnell","Daren","Darlene","Dariana","Darius","Darline","Darry","Darrion","Darold","Darby","Darren","Darryll","Darryn","Darvin","Dashawn","Daryn","Darwin","Dasia","Dave","Daryl","Daulton","Darwyn","Daryle","Davin","Daunte","Davie","Davon","Davion","Davonta","Davian","Davante","Davis","Davey","Davy","Davina","Dawne","Dawn","David","Deangelo","Dayna","Dax","Dayami","Dayne","Dayton","Dawna","Debera","Dean","Deann","Deacon","Debbra","Daxton","Dayle","Deanna","Deanne","Dayana","Deandra","Debbie","Davonte","Deb","Deasia","Deborrah","Dayanara","Deante","Debi","Deana","Dawson","Debby","Debbi","Dayse","Debrah","Deborah","Deane","Deandre","Deedee","Deeann","Deidra","Dee","Dejuan","Deion","Deidre","Debora","Debra","Deena","Debroah","Dedra","Dejah","Deetta","Deegan","Deja","Dedrick","Declan","Dejon","Dedric","Deforest","Delano","Delaney","Deirdre","Delia","Delilah","Del","Delfina","Delinda","Della","Deliah","Delisa","Delcie","Delina","Delbert","Delila","Dell","Dellar","Delle","Dellia","Delmer","Delmas","Dellie","Delmar","Delma","Delois","Deloris","Delmus","Delphine","Delphia","Delores","Delphin","Delton","Delsie","Delvin","Delwin","Dema","Delora","Delta","Delos","Demario","Delpha","Demarcus","Demetric","Demetrius","Demarion","Demond","Demetri","Demetria","Demi","Dempsey","Denis","Demetra","Dennis","Denita","Demonte","Denese","Denisha","Demarco","Denzell","Denise","Denton","Demian","Dena","Denisse","Demetrios","Denice","Deneen","Dennie","Denine","Denny","Derek","Dereon","Denzel","Deondre","Deon","Deonte","Deontae","Denver","Deonta","Denzil","Derald","Dequan","Derl","Deric","Dereck","Derrek","Desi","Deron","Derrell","Derick","Derik","Derrick","Derwin","Desirae","Desean","Deshaun","Deryl","Desiree","Deshawn","Devonta","Devan","Dessa","Deven","Destiney","Destin","Destry","Dessie","Devontae","Destany","Devaughn","Desmond","Devante","Devin","Destiny","Devon","Destinee","Destini","Devonte","Dewey","Dijon","Deward","Dewitt","Diana","Devyn","Dian","Dillard","Dexter","Diallo","Dezzie","Dicie","Dickie","Dianna","Diego","Diann","Dewayne","Diamond","Diandra","Dicy","Dillan","Deyanira","Dianne","Diane","Dillie","Dick","Dimitrios","Dink","Dinah","Dimitri","Dimple","Dillon","Dino","Dillion","Dina","Dion","Djuna","Dionte","Dixon","Dionne","Diya","Dione","Dirk","Dionicio","Dixie","Djuana","Dominic","Dollie","Dolly","Dola","Docia","Dolph","Domenico","Doll","Dominque","Doc","Doctor","Dollye","Domenica","Dolphus","Dominga","Dominik","Domonique","Dock","Domingo","Donald","Domenick","Dondre","Donaciano","Donato","Doloris","Dominick","Dona","Dolores","Donat","Domenic","Don","Dominique","Donal","Donta","Donn","Donnell","Dontae","Donna","Dorathea","Donavan","Donavon","Donell","Donia","Donovan","Donie","Donny","Donita","Donnie","Dorcas","Donte","Doreen","Dorathy","Dora","Dorene","Dori","Dorothea","Dorr","Doretha","Dorthey","Doretta","Dorla","Dorinda","Dorman","Dortha","Dorine","Dorris","Dorthy","Dorsey","Dorotha","Dorian","Dorthea","Doris","Dorothy","Doss","Dotty","Dozier","Doshie","Douglas","Doshia","Dottie","Draven","Dossie","Dosha","Dosia","Douglass","Dow","Dove","Dot","Doug","Dovie","Doyle","Drusilla","Drucilla","Duane","Drury","Duncan","Duke","Drema","Dudley","Drake","Duard","Dulce","Duff","Drew","Dusty","Durwood","Durell","Dustan","Dylan","Dustin","Durrell","Dulcie","Duwayne","Dwaine","Dwain","Dwyane","Durward","Dwight","Dwan","Dyllan","Dwane","Dylon","Dwayne","Dyan","Lamar","Lark","Lance","Lane","Lakesha","Lakeshia","Lala","Laci","Lahoma","Laila","Lanie","Lani","Lainey","Lakendra","Ladarius","Lafayette","Lanita","Lana","Laisha","Laron","Latarsha","Latanya","Larry","Laureen","Latrice","Latonia","Laurance","Laurence","Lady","Lafe","Landen","Lamonte","Laddie","Laquan","Landan","Lasonya","Laney","Landyn","Larissa","Lavern","Latoria","Lary","Latonya","Laura","Latifah","Lakisha","Lakeisha","Lacy","Lacie","Lamarcus","Laquita","Landin","Larue","Lars","Latesha","Lamont","Lannie","Latrina","Laurel","Lassie","Latoya","Laurine","Latisha","Laraine","Larkin","Lashawn","Lauri","Laverna","Laurette","Lavar","Latoyia","Latrell","Laurene","Lashunda","Laverne","Lavina","Lauryn","Lavera","Lacey","Laken","Lailah","Lalla","Ladonna","Larae","Lanny","Lanette","Landon","Lashanda","Lambert","Lashonda","Lara","Lauren","Latosha","Latasha","Latricia","Lavada","Laurie","Lavenia","Launa","Lavelle","Lauretta","Lavinia","Lavonia","Lavonne","Leana","Le","Lawanda","Lavonda","Lawton","Lawyer","Leafy","Lawrence","Layton","Lavon","Lawson","Leamon","Lawerence","Lavona","Lawrance","Layne","Layla","Laylah","Lea","Lazaro","Leandra","Leah","Leann","Leala","Leander","Leatrice","Leanne","Leandro","Leanna","Leaner","Leitha","Lelia","Leeann","Leigh","Leisa","Lelah","Leia","Lee","Leighton","Leilani","Leeroy","Leatha","Leda","Lelar","Lem","Leila","Leesa","Lela","Leif","Leisha","Leland","Lella","Lempi","Lemmie","Len","Lemma","Lemon","Lenard","Leo","Lennie","Lemuel","Lennon","Lenora","Lenord","Lena","Lenna","Lenny","Lenon","Lenore","Leola","Leonia","Leonor","Lera","Leonidas","Leon","Leona","Leonore","Leonce","Leoma","Leone","Leonie","Les","Leonard","Lesa","Leontine","Leroy","Leonardo","Leopold","Lenwood","Leslee","Leonel","Leonora","Leopoldo","Leora","Leota","Lesley","Lesia","Lesli","Letha","Lesly","Less","Lessie","Lester","Lesta","Leslie","Levi","Letitia","Letta","Levy","Levern","Leta","Leticia","Leva","Lethia","Levie","Levar","Letty","Lettie","Levina","Levin","Liller","Libby","Lex","Lillianna","Lewis","Liddie","Liana","Levon","Lida","Lew","Lillard","Leyla","Lexis","Lexie","Lilia","Lidie","Lexi","Lexus","Lilian","Liam","Libbie","Lilla","Liane","Lila","Lidia","Lilah","Lillian","Lillia","Liberty","Lige","Lia","Lilyan","Lilie","Lilburn","Lilly","Lilianna","Liliana","Lilliana","Lillis","Linda","Linzy","Linette","Lily","Lisa","Linna","Linus","Lina","Lillie","Lilyana","Lindbergh","Lindsay","Lindsey","Linden","Link","Lindy","Linton","Linnea","Lincoln","Lim","Linnie","Lisandro","Lindell","Linwood","Linn","Lisette","Lisha","Linsey","Lionel","Lissa","Lissette","Lisbeth","Lise","Lisle","Lish","Lita","Liston","Lissie","Litha","Liza","Little","Livia","Lizabeth","Littie","Littleton","Litzy","Liz","Lizbeth","Loda","Lizzie","Lollie","Lizeth","Lizette","Lloyd","Lois","Logan","Lockie","Llewellyn","Loma","Lolla","Lola","Lon","Lona","Lonny","Loney","Londyn","Lonzo","Long","London","Lolita","Lorelai","Lone","Lora","Loran","Lonna","Lonnie","Lorean","Loree","Loni","Lonie","Lorenz","Loreen","Lorene","Lorelei","Loren","Lorena","Loraine","Lorayne","Loria","Lorraine","Loriann","Loretto","Lorenzo","Lori","Lorine","Lorrie","Lorie","Loretta","Lorna","Lorinda","Lorin","Lorne","Lorrayne","Lorri","Lorenza","Lossie","Loris","Loring","Lott","Louanna","Lotta","Lota","Louetta","Louie","Louisa","Lottie","Lou","Louella","Loy","Lovina","Loyce","Louann","Louisiana","Louvenia","Lovell","Louise","Loula","Lovie","Lovey","Louis","Loyal","Lowell","Lovett","Lu","Lovisa","Love","Lourdes","Loyd","Luana","Luciano","Ludie","Lucetta","Luberta","Luanne","Lucie","Lucero","Lucas","Lugenia","Luc","Luca","Lucina","Luisa","Luann","Luciana","Lucian","Lucille","Lucy","Lue","Luetta","Lula","Luda","Lucia","Lucius","Lucien","Luigi","Luis","Ludwig","Lulie","Lucinda","Lucindy","Lucio","Lucretia","Lucile","Lukas","Luke","Lucky","Lucious","Luka","Luella","Lular","Lulah","Lynnette","Lum","Lurana","Lynda","Lute","Luster","Lyndsay","Luna","Lulla","Lura","Lupe","Lydell","Lurena","Luvinia","Lulu","Lynette","Luz","Lutie","Lydia","Lyman","Lyla","Lurline","Luverne","Lynsey","Luther","Lyda","Lyle","Lyndia","Lynwood","Lyndsey","Lyndon","Luvenia","Lyn","Lynn","Lynne","Lyric","Cap","Caldonia","Camilla","Cali","Carma","Candice","Calvin","Cael","Cari","Cadence","Carlisle","Carie","Camille","Caleigh","Carolynn","Caddie","Caleb","Carla","Cammie","Carlos","Carlo","Caesar","Caitlynn","Candi","Caitlyn","Candy","Campbell","Carleigh","Calla","Carolee","Captain","Canyon","Callie","Carisa","Caiden","Camila","Carey","Carmel","Carin","Calista","Cale","Carol","Candis","Carleton","Carlie","Candida","Camryn","Cade","Carmelita","Carmine","Camisha","Carissa","Cailyn","Caroline","Capitola","Carole","Candace","Cannon","Cappie","Callum","Carlee","Caitlin","Cami","Caro","Carley","Carolann","Carmela","Carl","Cain","Candido","Carleen","Cam","Camden","Cal","Carnell","Caden","Caprice","Candyce","Carly","Carmella","Carlyle","Carlene","Cara","Carmelo","Camron","Cameron","Calhoun","Carli","Carlton","Carlyn","Caron","Carina","Carlotta","Carolina","Cannie","Carlota","Carmen","Caren","Carolyn","Carolyne","Camren","Carra","Carri","Carroll","Carrol","Carsen","Carter","Carrie","Carson","Caryl","Casimer","Carry","Cary","Cas","Casandra","Case","Caryn","Casper","Cassandra","Cassie","Casimiro","Casey","Cassondra","Casie","Catalina","Cathy","Cash","Cass","Cathern","Cassius","Cathey","Cason","Cato","Caswell","Catharine","Cassidy","Cathie","Cathleen","Catherine","Cecil","Cathryn","Casimir","Catrina","Caylee","Cayden","Cecelia","Cathrine","Cedric","Ceasar","Cathi","Catina","Cecily","Celeste","Ceil","Cecile","Cecilia","Cayla","Ceola","Celia","Celena","Cedrick","Celie","Celestia","Celesta","Chance","Chanda","Ceylon","Channing","Chadd","Chancy","Chad","Celina","Channie","Celestino","Cephus","Chanel","Cesar","Chandler","Celestine","Chantelle","Chancey","Celine","Chalmers","Chaka","Chandra","Chadrick","Cena","Chalmer","Chaim","Chadwick","Chana","Chace","Champ","Chanelle","Charisse","Chantal","Chantel","Chanie","Chaney","Charissa","Chante","Charity","Charla","Charleen","Charline","Charle","Charlie","Charlene","Charlee","Charles","Charlize","Charolette","Charlsie","Chas","Charlottie","Charls","Charlton","Charley","Charlotta","Charly","Charlotte","Charmaine","Chaz","Chelsi","Cherilyn","Che","Chasity","Chastity","Cherise","Chase","Cher","Chauncey","Chelsea","Chauncy","Cherie","Chaya","Cheri","Cherry","Chelsey","Chelsy","Chelsie","Cherrie","Cherish","Cherelle","Cheryle","Cheryll","Christ","Chestina","Cherrelle","Chessie","Chet","Cherri","Chesley","Cheyanne","Chester","Chloe","Cherryl","Chiquita","Chimere","Chin","Cheryl","Cheyenne","Chrissy","Christeen","China","Chrissie","Christel","Chip","Chloie","Christa","Chris","Christop","Christion","Christopher","Christen","Christine","Chrystal","Christian","Christie","Christoper","Ciara","Cicely","Christin","Christina","Christi","Christal","Cielo","Christiana","Christene","Christena","Chuck","Christy","Chynna","Cicero","Cilla","Ciji","Cinda","Ciera","Ciarra","Cierra","Chyna","Cindi","Cinthia","Cindy","Citlali","Citlalli","Cinnamon","Claire","Clare","Clarabelle","Clarice","Clabe","Claiborne","Clarance","Clair","Clarence","Claribel","Clara","Clarinda","Clarissa","Clarisa","Clarine","Claud","Claudie","Clearence","Claude","Cleda","Clarnce","Claudia","Clark","Clarke","Claudette","Claudio","Classie","Cleave","Clementina","Clemma","Claudine","Clell","Clay","Clement","Claudius","Claus","Clemence","Clayton","Clem","Clemente","Clemens","Clella","Clemie","Cleone","Cliff","Cleon","Cleta","Clemon","Cleveland","Clemmie","Cleola","Cleve","Clementine","Cleora","Cleva","Cleo","Clevie","Cletus","Clora","Colby","Clyda","Clifford","Cloyd","Colleen","Cody","Cliffie","Clifton","Coletta","Clive","Clinton","Clint","Codey","Clide","Coen","Clotilde","Coleton","Colbert","Clydie","Cohen","Clotilda","Coleen","Codie","Clovis","Colette","Cloe","Coleman","Cole","Coby","Clyde","Codi","Clytie","Coley","Colie","Colin","Collette","Collins","Collie","Colt","Collis","Collier","Collin","Colon","Colonel","Columbus","Colten","Con","Colter","Commodore","Conard","Concetta","Colton","Colvin","Columbia","Concepcion","Conor","Conner","Conway","Consuela","Conrad","Conley","Connie","Connor","Coolidge","Coralie","Constantine","Concha","Contina","Coral","Constance","Consuelo","Cooper","Corbett","Corie","Cordelia","Corbin","Cordaro","Cori","Corinna","Corinne","Cordell","Cornelius","Cordella","Corina","Coraima","Coretta","Corda","Cordero","Cordia","Cora","Corey","Corean","Cordie","Corine","Corene","Cornie","Cornelious","Cory","Cornelia","Cornel","Corrina","Corliss","Corrine","Cortez","Corry","Corrie","Corwin","Cornell","Courtland","Cristina","Cortney","Coty","Coy","Cristobal","Cristal","Creola","Crawford","Courtney","Crete","Cosmo","Craig","Creed","Council","Crissy","Cristopher","Cressie","Cristian","Cris","Crissie","Cristi","Crista","Cristofer","Cristine","Cristen","Cristin","Crysta","Crystal","Curtiss","Cuba","Cyndi","Curley","Cruz","Cullen","Cristy","Crockett","Curt","Curtis","Cyrus","Cyntha","Cyril","Cydney","Cynthia","Beadie","Belle","Bertha","Baxter","Beatrix","Bee","Barry","Bell","Babymale","Berkley","Bertina","Basil","Baker","Barnie","Barrett","Banks","Barnard","Bartholomew","Barbara","Beckham","Barbie","Barb","Baby","Benjamen","Bella","Barnett","Berry","Bernadine","Barrie","Beaulah","Benny","Beatriz","Bascom","Bennett","Belen","Bambi","Belia","Bart","Benito","Benjman","Babe","Bayard","Berdie","Bennie","Benson","Beatrice","Bailey","Beckett","Bailee","Beda","Belva","Bertie","Benton","Bama","Bea","Benard","Baron","Benjamine","Berta","Belton","Benedict","Beecher","Barbra","Barney","Becky","Baylee","Benji","Babette","Berlin","Bartley","Bernice","Belinda","Bebe","Baldwin","Bedford","Ben","Bernetta","Bernita","Bert","Bena","Berniece","Bernardo","Beau","Bernie","Baylie","Bernhard","Beckie","Ballard","Bernadette","Bernardine","Berneice","Berenice","Benjamin","Benjiman","Barton","Benjaman","Barron","Benita","Bentley","Bernard","Berton","Bertrand","Besse","Bessie","Beula","Bertram","Betty","Bianca","Beryl","Beth","Bethann","Betsey","Bethel","Betha","Bettye","Beverly","Betsy","Bess","Bethany","Bev","Bettyjane","Bethzy","Bettie","Bette","Beulah","Beverlee","Beyonce","Bettina","Bettylou","Blane","Biddie","Birdella","Billye","Blain","Bilal","Blake","Bird","Birtha","Bill","Birt","Bishop","Blanca","Blanche","Bina","Billy","Birdie","Beverley","Blanchard","Bjorn","Blanch","Birtie","Blaise","Blas","Billie","Blaze","Bobbi","Blair","Blaine","Blanchie","Bliss","Blossom","Bonny","Bobbye","Bob","Bradford","Boss","Bonita","Booker","Bobby","Bluford","Bowman","Bolden","Braden","Bradyn","Bobbie","Bode","Bo","Brande","Bradley","Braeden","Braedon","Brad","Bradly","Boone","Boris","Braiden","Boston","Braelyn","Bose","Bonnie","Brady","Brandee","Brandin","Brain","Brandan","Branch","Brandie","Branden","Brandon","Brandt","Brandi","Brea","Braydon","Brayan","Brendon","Brandy","Brenda","Brant","Breana","Breann","Brenden","Branson","Braxton","Brandyn","Brannon","Braylon","Braulio","Braylen","Breanna","Breanne","Bree","Brantley","Brayden","Brett","Brennan","Brendan","Brennen","Brennon","Brent","Breonna","Brenna","Bria","Briana","Brianna","Brenton","Brian","Brionna","Brianda","Brigette","Britany","Briley","Britny","Bridgette","Brittaney","Brittni","Brittanie","Brien","Bret","Brianne","Bridgett","Brigitte","Britta","Brice","Brinda","Brion","Bridger","Bridget","Britt","Brisa","Britney","Brigid","Brielle","Brittnay","Brittnee","Britni","Brittany","Brooke","Brodie","Britton","Broderick","Brock","Brittney","Brogan","Bryanna","Brooklyn","Brittani","Brook","Brittnie","Brittny","Brown","Brooklynn","Brody","Bryce","Bronson","Bryant","Bruce","Bryn","Bryan","Bryton","Brynn","Bruno","Bryon","Bryana","Brycen","Brooks","Brynlee","Bud","Budd","Bryson","Buck","Brylee","Buffy","Buel","Buddie","Buelah","Buddy","Buell","Buena","Burl","Buford","Burgess","Bulah","Burdette","Burke","Burnell","Buna","Bula","Burk","Bunk","Buren","Burley","Burnett","Burleigh","Burt","Burr","Burton","Burney","Burnie","Burns","Bynum","Burrell","Burnice","Bush","Burrel","Butch","Byrdie","Byrd","Butler","Byron","Buster","Randell","Rafe","Rakeem","Raekwon","Rahn","Randy","Rahsaan","Rahul","Renae","Raymond","Randal","Randall","Rand","Reba","Rayburn","Ragna","Raphael","Ramon","Raiden","Raquan","Raegan","Raina","Rayfield","Reilly","Racquel","Rance","Raquel","Reason","Rachael","Redden","Ransom","Ramona","Ras","Racheal","Redmond","Rachel","Regena","Rashawn","Randel","Reese","Rashaan","Raul","Ralph","Red","Rashida","Raleigh","Reed","Rayshawn","Ramsey","Rella","Raymon","Reinhold","Renada","Rayford","Randolph","Randolf","Raheem","Raynard","Rena","Rebecca","Reatha","Randi","Reino","Rafaela","Reginald","Raven","Reggie","Rafael","Raoul","Regenia","Raelynn","Rachelle","Regina","Rashad","Remington","Reinaldo","Rebekah","Rebeca","Regan","Randle","Rene","Ray","Rashaad","Rayna","Reagan","Reanna","Renata","Ramiro","Reece","Rae","Rayan","Rasheed","Reina","Raymundo","Renard","Renaldo","Regis","Rayne","Refugio","Reginal","Reid","Renee","Reubin","Rexford","Reynold","Ressie","Reynaldo","Renea","Renita","Rennie","Rhea","Reva","Retha","Reno","Reuben","Rex","Rhianna","Reyes","Rey","Rheta","Reyna","Reta","Rettie","Rhett","Retta","Reynolds","Rhys","Richmond","Rhiannon","Rhonda","Richard","Rice","Rhoda","Rhona","Rian","Ricci","Rianna","Ricardo","Rich","Ridge","Richie","Rick","Richelle","Rickey","Rikki","Ricky","Ricki","Rickie","Riley","Rinda","Rigoberto","Rico","Rillie","Rita","River","Rihanna","Risa","Rilla","Riya","Rishi","Ritchie","Robby","Robin","Roberto","Rodger","Roberta","Robbin","Robyn","Robt","Robert","Roll","Robb","Roddy","Robley","Roger","Rob","Rocky","Roland","Roderic","Roby","Rochelle","Rocco","Rolla","Rocio","Rod","Roena","Rodney","Rolanda","Rock","Robbie","Roe","Roderick","Rodolfo","Rogelio","Rolando","Rodrick","Rolf","Rodrigo","Roel","Rohan","Rogers","Rollin","Roman","Romello","Rolland","Rome","Rollo","Romeo","Romaine","Roma","Rollie","Romie","Ronny","Rosaline","Rondal","Ronda","Romona","Ron","Roni","Ronald","Ronna","Ronal","Rosalinda","Rona","Ronnie","Ronan","Roosevelt","Rosalyn","Rosabelle","Ronaldo","Rosa","Rory","Rosalind","Rosalia","Ronin","Rosalee","Rosanna","Rosanne","Rosalie","Roseann","Rosella","Rosemary","Rosann","Rosco","Rosie","Rosetta","Roselyn","Roseanna","Roseanne","Rosaria","Rosamond","Roscoe","Rose","Rosendo","Rosario","Rosemarie","Rosevelt","Rosena","Roslyn","Rosey","Rowland","Rosia","Ross","Rosina","Royce","Rowan","Rozella","Roxy","Rubi","Roxanna","Roxie","Rossie","Rosita","Roxana","Roy","Rosy","Royal","Roxanne","Roswell","Ruben","Rowena","Roxane","Roxann","Rozanne","Rufus","Rudolfo","Rube","Rudolph","Rush","Rubye","Rupert","Rueben","Rubin","Ruie","Rubie","Ruffus","Rudolf","Rudy","Ruel","Ruby","Ruffin","Rusty","Rutha","Russel","Russell","Ruthe","Ruthann","Rustin","Ruthanne","Ruth","Russ","Ryann","Ryland","Ryley","Ruthie","Rylan","Ryan","Ryder","Ryleigh","Ryne","Ryker","Rylie","Rutherford","Rylee","Pamelia","Pandora","Pam","Pallie","Pinkie","Pearly","Pablo","Palmer","Paralee","Park","Paityn","Pershing","Paul","Paola","Pearle","Palma","Pascal","Pauletta","Pepper","Parrish","Parlee","Pamala","Peggie","Perri","Pauline","Patric","Penni","Peggy","Patrice","Pamela","Paloma","Petra","Pat","Patrick","Payten","Paisley","Pamella","Pansy","Parthenia","Paige","Pearley","Pennie","Pearlene","Pierce","Penny","Pearla","Phylis","Phoenix","Pearl","Parley","Page","Perry","Perla","Percy","Pattie","Perley","Patty","Philo","Paula","Permelia","Paris","Phil","Patrica","Parker","Phebe","Phoebe","Percival","Phyliss","Pairlee","Patsy","Payton","Patricia","Peter","Peyton","Pate","Pasquale","Philip","Paulo","Philomene","Piper","Paxton","Pink","Pete","Patti","Phyllis","Pierre","Paulina","Pheobe","Pearlie","Philomena","Phillip","Phylicia","Paulette","Pearline","Pinkey","Pernell","Penelope","Patience","Pinkney","Phillis","Pedro","Pleas","Plummer","Pleasant","Pluma","Ples","Pratt","Porsche","Porfirio","Posey","Pollie","Pricilla","Polk","Portia","Porsha","Polly","Porter","Precious","Pranav","Press","Powell","Prentice","Prentiss","Price","Purl","Prince","Princess","Preston","Priscilla","Priscila","Prudence","Presley","Prosper","Prudie","Primus","Pryor","Olena","Odette","Odile","Odie","Olga","Oneal","Oliva","Orin","Oddie","Olympia","Olevia","Oakley","Oleta","Onnie","Orlin","Obed","Octa","Ole","Orilla","Orene","Oneida","Oliver","Octavius","Odalys","Oda","Obie","Ora","Octave","Omer","Orris","Olive","Omari","Ollie","Onie","Orson","Ocie","Ola","Oneta","Olan","Odus","Oralia","Oline","Okey","Odin","Orange","Orion","Olin","Odell","Orie","Octavie","Oley","Orma","Odis","Orpha","Oran","Odalis","Omar","Octavia","Omie","Olinda","Oney","Odelia","Oland","Ogden","Olene","Oris","Orah","Orren","Oral","Orlena","Olivia","Olaf","Oma","Offie","Opal","Orval","Orvel","Ona","Obe","Opha","Ofelia","Orra","Olie","Olar","Omarion","Orley","Orvil","Orelia","Ophelia","Orlo","Octavio","Odessa","Orla","Olivine","Oren","Olen","Orrin","Orlando","Olof","Orrie","Orland","Orville","Osbaldo","Osa","Otto","Osie","Orvin","Ottie","Osborne","Osborn","Otho","Ottis","Otha","Ota","Oswald","Oscar","Ottilia","Orvis","Othel","Ozie","Otis","Ozzie","Otelia","Ottilie","Osvaldo","Ovid","Ossie","Oswaldo","Owens","Ozell","Ova","Ott","Ozella","Owen","Ouida","Ovila","Ilona","Ian","Idabelle","Ingeborg","Ica","Ivan","Iliana","Ivah","Ieshia","Ira","Iridian","Irven","Irl","Ibrahim","Inger","Ines","Idamae","Irving","Ilene","Ione","Iona","Ireland","Ida","Idell","Isa","Idella","Imanol","Icy","Iesha","Irene","Irwin","Isabela","Ilo","Ike","Illa","Ina","Isabel","Isla","Irine","Immanuel","Inell","Ishaan","Isam","Ima","Isidore","Indiana","Ishmael","Isadora","Isaac","Illya","Inga","Imogene","Icie","Inez","Ilma","Icey","Irena","Imelda","Isham","Ingrid","Itzel","Isidro","Isiah","Isai","India","Ismael","Issac","Isobel","Ingram","Imo","Iva","Imani","Isamar","Ilda","Isabella","Ilah","Isaias","Irva","Iola","Iris","Ila","Isabelle","Isadore","Irma","Ignacio","Irvin","Ignatius","Iverson","Iver","Ignatz","Isaiah","Infant","Isom","Ivana","Isidor","Irvine","Israel","Isabell","Isaak","Isis","Isreal","Ivie","Ivette","Ivey","Ivor","Ivonne","Ivy","Iza","Izayah","Iyana","Izetta","Izola","Ivory","Izaiah","Izora","Iyanna","Izabella","Izabelle","Tab","Tal","Talmage","Tammi","Tabitha","Takisha","Tanner","Tami","Tai","Tamica","Tangela","Tanesha","Tamela","Taja","Tamisha","Tarsha","Talmadge","Tenika","Tayler","Tasia","Tatyanna","Tatsuo","Tavion","Taniya","Tate","Taylor","Taurean","Tashina","Tamia","Tamya","Telly","Talia","Tammy","Taurus","Tawanna","Tatianna","Teena","Tea","Taya","Tamala","Tanika","Talen","Tabetha","Tamra","Tamera","Talon","Tammie","Tania","Tara","Tayshaun","Tasha","Tatia","Tawnya","Tari","Tatum","Teagan","Taryn","Tena","Taniyah","Tahj","Tamekia","Tambra","Tad","Tamika","Tameka","Tabatha","Taina","Tallie","Tatiana","Tandy","Teddy","Tanja","Tavian","Teddie","Tayla","Tawny","Tawanda","Tavares","Taliyah","Taj","Taft","Tana","Tamiko","Tamatha","Tamara","Tanisha","Talan","Tamie","Tanya","Tariq","Tarik","Tarah","Tavon","Tella","Tatyana","Tavaris","Tawana","Tegan","Ted","Teela","Tempie","Tera","Tenisha","Tennessee","Tennie","Tennille","Teresa","Terence","Terese","Terance","Terell","Terrance","Teri","Terrell","Terra","Teressa","Terri","Terrie","Terry","Terrence","Terrill","Tex","Tess","Tessie","Tevin","Tessa","Texie","Thad","Texanna","Texas","Thaddeus","Theda","Thea","Thedore","Theadore","Thalia","Thekla","Theodis","Theodocia","Theo","Thelma","Theophile","Theodora","Theodosia","Theola","Theodore","Theresia","Theresa","Theron","Therese","Therman","Thomas","Thor","Theta","Thomasina","Thompson","Thos","Thornton","Thora","Thorwald","Thresa","Thurman","Thurlow","Thursa","Thyra","Thurston","Tiarra","Tia","Tianna","Tiana","Tiara","Tiera","Tiffani","Tiffanie","Tiesha","Tierra","Tilda","Tilden","Tillie","Tilla","Tiffany","Tillman","Tilman","Timmie","Tim","Timmothy","Tina","Tinie","Timothy","Timmy","Tiney","Tishie","Tisa","Tisha","Tiny","Tinnie","Tobi","Titus","Tito","Tobias","Tobe","Tobin","Toccara","Tod","Toby","Tobie","Todd","Toivo","Tom","Tolbert","Tollie","Tomas","Tomasa","Tomeka","Toma","Tomie","Toney","Tommy","Tomika","Tonda","Tommie","Tony","Tonya","Tonja","Tonia","Toni","Toriano","Tori","Torie","Torey","Torrance","Torrey","Torrence","Torry","Tory","Torrie","Tosha","Toy","Towanda","Toshio","Toshiko","Tracey","Trace","Tracee","Toya","Traci","Trae","Tracie","Tracy","Travis","Travon","Treasure","Trayvon","Tremaine","Treena","Tre","Trena","Tremayne","Trent","Trenton","Trenten","Tresa","Tressie","Trever","Treva","Tressa","Trey","Trevin","Trevor","Trevion","Trevon","Treyvon","Trilby","Trina","Tricia","Treyton","Trisha","Trish","Tripp","Trinity","Trinidad","Trista","Tristin","Tristian","Tristen","Tristan","Trudie","Trudy","Trudi","Triston","Truman","Troy","Tuan","Trula","Trystan","Twyla","Trumaine","Twila","Turner","Tucker","Tula","Tye","Tylor","Ty","Tyesha","Tyree","Tyler","Tyra","Tyquan","Tyreek","Tyreke","Tyreese","Tyrel","Tyrell","Tyrese","Tyrek","Tyriq","Tyrin","Tyrik","Tyrus","Tyshawn","Tyrique","Tyron","Tyson","Tyrone","Sadye","Salina","Samir","Sandy","Sabastian","Samara","Sabra","Sal","Samuel","Sammy","Sarita","Santiago","Sabina","Sanford","Salvador","Sampson","Samson","Sallie","Sanaa","Sandra","Sarai","Sandi","Salvatore","Sable","Sarrah","Sammie","Samira","Santa","Samual","Sanjuana","Sanjuanita","Samantha","Santo","Saverio","Sada","Sage","Saul","Saniya","Sade","Samatha","Sam","Samie","Saniyah","Sally","Salomon","Sabrina","Sasha","Sandie","Santino","Sarina","Savilla","Savon","Selmer","Seaborn","Serena","Salome","Santina","Schuyler","Salma","Sanders","Saint","Sariah","Sanai","Scott","Saige","Sadie","Seldon","Sarah","Santos","Salena","Savana","Selene","Selina","Savannah","Sedrick","Scot","Savanna","Seneca","Sannie","Scotty","Santana","Savanah","Savion","Scarlet","Sara","Sarahi","Sebastian","Sebrina","Selena","Scottie","Schley","Scarlett","Saundra","Selma","Seamus","Selah","Senora","Semaj","Sean","Sawyer","Sena","Serenity","Seth","Severt","Seward","Shad","Serina","Sergio","Shade","Severo","Seymour","Shamika","Shae","Shafter","Shakira","Shan","Shaina","Shalonda","Shanda","Shamar","Shalon","Shana","Shameka","Shanae","Shantel","Shanelle","Shanequa","Sharday","Shari","Shanna","Shania","Shandra","Shanon","Shannon","Shani","Shanell","Shaneka","Sharee","Shane","Shaquana","Shannen","Shanice","Shantell","Shaquita","Shanta","Shaniya","Shaquille","Shaniece","Shara","Shaquan","Sharde","Shante","Shanika","Shardae","Shaniqua","Sharif","Shannan","Shanita","Sharita","Sharyl","Sharman","Sharleen","Sharla","Sharen","Sharlene","Sharyn","Shayne","Shauna","Sharon","Shawnna","Shelley","Sheena","Shavon","Shayna","Sheilah","Shawna","Sharron","Shayla","Sharonda","Shay","Shaunna","Shawn","Shawnda","Shelbi","Shaylee","Shasta","Shavonne","Shawanda","Shelli","Shelbie","Shaun","Shelba","Shelia","Shedrick","Shawnte","Shea","Shatara","Shawnee","Shelby","Sheila","Sheldon","Shelvia","Sherri","Shelly","Shepherd","Shelton","Shenna","Shelva","Sheree","Sherie","Shena","Sherlyn","Shemar","Shellie","Shep","Shelvie","Sheron","Sherita","Sheridan","Sherman","Sheri","Sherilyn","Shirleen","Shonda","Sherrill","Sheryll","Sheyla","Sherree","Sherrie","Shira","Sherwin","Sherron","Sherwood","Shirleyann","Shianne","Shiela","Sherryl","Sheryl","Shon","Shiloh","Shoji","Sherry","Shyheim","Shirlee","Shirl","Shirlene","Shonna","Shirley","Siddie","Shirlie","Sibbie","Sibyl","Shreya","Shyann","Sierra","Shona","Sid","Simeon","Sidney","Shyla","Simona","Silver","Siena","Simpson","Slade","Siobhan","Signa","Silvio","Sister","Sinda","Silas","Shyanne","Sigurd","Sina","Sigrid","Sie","Sky","Simone","Sigmund","Sol","Sim","Signe","Sienna","Silvester","Simmie","Silvia","Smith","Solomon","Sincere","Skyla","Sing","Sloane","Soloman","Skylar","Socorro","Skip","Skyler","Skye","Simon","Sofia","Soledad","Solon","Son","Soren","Sonji","Sommer","Sonya","Stacey","Somer","Squire","Sonja","Sophia","Sonny","Sonia","Spring","Spenser","Sondra","Spurgeon","Spencer","Sophie","Sophronia","Staci","Sterling","Stacy","Stasia","Stan","Stanislaus","Stacia","Starr","Stacie","Stephanie","Stafford","Stella","Starla","Stefan","Stanford","Stephenie","Star","Stephani","Stephan","Stanley","Stanton","Stephany","Starling","Stefanie","Stetson","Stephaine","Stephania","Stefani","Stephen","Stephon","Stone","Stevan","Steward","Stonewall","Stevie","Steve","Stoney","Storm","Steven","Stormy","Stewart","Susannah","Sullivan","Summer","Sumner","Sudie","Susanne","Susan","Sunday","Suzanne","Suellen","Sue","Suzan","Sula","Susanna","Susann","Sybilla","Susana","Stuart","Susie","Sydnee","Suzanna","Syble","Sunshine","Sussie","Suzann","Sydni","Sunny","Suzie","Suzette","Suzy","Sydnie","Sydell","Sybil","Sylvania","Sylvan","Sylva","Sydney","Sylvanus","Syed","Symone","Sylvia","Sylvester","Syreeta"};
       String[] lastName = new String[] {"Anderson", "Ashwoon", "Aikin", "Bateman", "Bongard", "Bowers", "Boyd", "Cannon", "Cast", "Deitz", "Dewalt", "Ebner", "Frick", "Hancock", "Haworth", "Hesch", "Hoffman", "Kassing", "Knutson", "Lawless", "Lawicki", "Mccord", "McCormack", "Miller", "Myers", "Nugent", "Ortiz", "Orwig", "Ory", "Paiser", "Pak", "Pettigrew", "Quinn", "Quizoz", "Ramachandran", "Resnick", "Sagar", "Schickowski", "Schiebel", "Sellon", "Severson", "Shaffer", "Solberg", "Soloman", "Sonderling", "Soukup", "Soulis", "Stahl", "Sweeney", "Tandy", "Trebil", "Trusela", "Trussel", "Turco", "Uddin", "Uflan", "Ulrich", "Upson", "Vader", "Vail", "Valente", "Van Zandt", "Vanderpoel", "Ventotla", "Vogal", "Wagle", "Wagner", "Wakefield", "Weinstein", "Weiss", "Woo", "Yang", "Yates", "Yocum", "Zeaser", "Zeller", "Ziegler", "Bauer", "Baxster", "Casal", "Cataldi", "Caswell", "Celedon", "Serra", "Giannini", "Binchi", "Rossi", "Chambers", "Chapman", "Christensen", "Darnell", "Davidson", "Davis", "DeLorenzo", "Dinkins", "Doran", "Dugelman", "Dugan", "Duffman", "Eastman", "Ferro", "Ferry", "Fletcher", "Fietzer", "Hylan", "Hydinger", "Illingsworth", "Ingram", "Irwin", "Jagtap", "Jenson", "Johnson", "Johnsen", "Jones", "Jurgenson", "Kalleg", "Kaskel", "Keller", "Leisinger", "LePage", "Lewis", "Linde", "Lulloff", "Maki", "Martin", "McGinnis", "Mills", "Moody", "Moore", "Napier", "Nelson", "Norquist", "Nuttle", "Olson", "Ostrander", "Reamer", "Reardon", "Reyes", "Rice", "Ripka", "Roberts", "Rogers", "Root", "Sandstrom", "Sawyer", "Schlicht", "Schmitt", "Schwager", "Schutz", "Schuster", "Tapia", "Thompson", "Tiernan", "Tisler"};

       String[] firstName = new String[] {"Aaden","Aaliyah","Aarav","Aaron","Ab","Abagail","Abb","Abbey","Abbie","Abbigail","Abbott","Abby","Abdiel","Abdul","Abdullah","Abe","Abel","Abelardo","Abie","Abigail","Abigale","Abigayle","Abner","Abraham","Abram","Abril","Ace","Acey","Achsah","Acie","Acy","Ada","Adah","Adalberto","Adaline","Adalyn","Adalynn","Adam","Adamaris","Adams","Adan","Add","Adda","Addie","Addison","Addisyn","Addyson","Adel","Adela","Adelaide","Adelard","Adelbert","Adele","Adelia","Adelina","Adeline","Adell","Adella","Adelle","Adelyn","Aden","Adilene","Adin","Adina","Adison","Aditya","Adlai","Adline","Admiral","Adolf","Adolfo","Adolph","Adolphus","Adonis","Adrain","Adria","Adrian","Adriana","Adriane","Adrianna","Adrianne","Adriel","Adrien","Adriene","Adrienne","Adron","Adyson","Aedan","Affie","Afton","Agatha","Aggie","Agnes","Agness","Agusta","Agustin","Agustus","Ah","Ahmad","Ahmed","Aida","Aidan","Aiden","Aidyn","Aileen","Ailene","Aili","Aimee","Ainsley","Aisha","Aiyana","Aja","Akeelah","Akeem","Akira","Al","Ala","Alabama","Alaina","Alan","Alana","Alani","Alanna","Alannah","Alanzo","Alayna","Alba","Albert","Alberta","Albertha","Albertina","Albertine","Alberto","Albertus","Albin","Albina","Albion","Alby","Alcee","Alcide","Alcie","Alda","Alden","Aldo","Aldona","Aleah","Alease","Alec","Alecia","Aleck","Aleen","Aleena","Alejandra","Alejandro","Alek","Alena","Alene","Alesha","Alesia","Alessandra","Alessandro","Aleta","Aletha","Alethea","Alex","Alexa","Alexande","Alexander","Alexandr","Alexandra","Alexandre","Alexandrea","Alexandria","Alexandro","Alexia","Alexina","Alexis","Alexus","Alexys","Alexzander","Alf","Alferd","Alfie","Alfonse","Alfonso","Alfonzo","Alford","Alfred","Alfreda","Alfredo","Alger","Algernon","Algie","Algot","Ali","Alia","Aliana","Alice","Alicia","Alida","Alijah","Alina","Aline","Alisa","Alisha","Alison","Alissa","Alisson","Alivia","Aliya","Aliyah","Aliza","Alize","Alla","Allan","Allean","Alleen","Allen","Allena","Allene","Allie","Alline","Allison","Allisson","Ally","Allyn","Allyson","Allyssa","Alma","Almeda","Almedia","Almer","Almeta","Almina","Almira","Almon","Almond","Almus","Almyra","Alois","Aloma","Alondra","Alonso","Alonza","Alonzo","Aloys","Aloysius","Alpha","Alpheus","Alphons","Alphonse","Alphonsine","Alphonso","Alphonsus","Alston","Alta","Altha","Althea","Altie","Alto","Alton","Alva","Alvah","Alvan","Alvaro","Zakary","Zella","Zariah","Zack","Zaria","Zaiden","Zayden","Zadie","Zachariah","Zona","Zed","Zachery","Zola","Zaniyah","Zada","Zettie","Zeke","Zandra","Zoey","Zackery","Zaida","Zara","Zain","Zechariah","Zenas","Zayne","Zelda","Zebulon","Zana","Zenobia","Zoa","Zita","Zachary","Zelpha","Zavier","Zackary","Zula","Zillah","Zander","Zelia","Zaid","Zhane","Zane","Zeno","Zaire","Zena","Zeb","Zeta","Zetta","Zilpah","Zoe","Zela","Zilpha","Zoie","Zora","Zigmund","Zelma","Zion","Zollie","Zina","Ula","Urijah","Unique","Urban","Una","Uriel","Ura","Uriah","Ulysses","Ursula","Ulises","Vaughn","Vashon","Valentine","Velda","Verda","Vernal","Valerie","Venessa","Veronica","Verner","Vera","Vashti","Verl","Val","Vance","Vester","Velva","Vada","Venice","Vassie","Vernita","Valery","Vernie","Verlyn","Verna","Vidal","Veva","Vesta","Verona","Verne","Verena","Vicky","Valorie","Valinda","Valentino","Verla","Vicente","Vennie","Verlie","Vernon","Verlene","Vickey","Vara","Versie","Versa","Vernell","Verlin","Victoria","Van","Velvet","Vela","Verdie","Valencia","Veda","Valarie","Vessie","Vergie","Verdell","Vannie","Vergil","Vere","Vertie","Vikki","Vicy","Verle","Vicie","Vallie","Victory","Valentin","Valeria","Vella","Vicki","Vernice","Valentina","Vern","Vena","Vanesa","Vick","Vernia","Venie","Veta","Verlon","Venita","Vanessa","Vickie","Vina","Victor","Venus","Vander","Veola","Vilma","Velia","Victoriano","Vernelle","Villa","Vernetta","Victorine","Vince","Velma","Vida","Vic","Vincenzo","Vincent","Vinie","Vincenza","Viney","Viola","Vinnie","Violet","Vinton","Vinson","Violette","Violeta","Violetta","Vira","Virdie","Virge","Virgil","Virgie","Virgia","Virgel","Vita","Virgle","Viridiana","Virginia","Vito","Viva","Vivien","Vivian","Viviana","Vivienne","Vollie","Vonda","Volney","Vlasta","Von","Vonetta","Vonnie","Queenie","Quiana","Quentin","Quinten","Quint","Quinn","Qiana","Queen","Quintin","Quincy","Quinton","Lamar","Lark","Lance","Lane","Lakesha","Lakeshia","Lala","Laci","Lahoma","Laila","Lanie","Lani","Lainey","Lakendra","Ladarius","Lafayette","Lanita","Lana","Laisha","Laron","Latarsha","Latanya","Larry","Laureen","Latrice","Latonia","Laurance","Laurence","Lady","Lafe","Landen","Lamonte","Laddie","Laquan","Landan","Lasonya","Laney","Landyn","Larissa","Lavern","Latoria","Lary","Latonya","Laura","Latifah","Lakisha","Lakeisha","Lacy","Lacie","Lamarcus","Laquita","Landin","Larue","Lars","Latesha","Lamont","Lannie","Latrina","Laurel","Lassie","Latoya","Laurine","Latisha","Laraine","Larkin","Lashawn","Lauri","Laverna","Laurette","Lavar","Latoyia","Latrell","Laurene","Lashunda","Laverne","Lavina","Lauryn","Lavera","Lacey","Laken","Lailah","Lalla","Ladonna","Larae","Lanny","Lanette","Landon","Lashanda","Lambert","Lashonda","Lara","Lauren","Latosha","Latasha","Latricia","Lavada","Laurie","Lavenia","Launa","Lavelle","Lauretta","Lavinia","Lavonia","Lavonne","Leana","Le","Lawanda","Lavonda","Lawton","Lawyer","Leafy","Lawrence","Layton","Lavon","Lawson","Leamon","Lawerence","Lavona","Lawrance","Layne","Layla","Laylah","Lea","Lazaro","Leandra","Leah","Leann","Leala","Leander","Leatrice","Leanne","Leandro","Leanna","Leaner","Leitha","Lelia","Leeann","Leigh","Leisa","Lelah","Leia","Lee","Leighton","Leilani","Leeroy","Leatha","Leda","Lelar","Lem","Leila","Leesa","Lela","Leif","Leisha","Leland","Lella","Lempi","Lemmie","Len","Lemma","Lemon","Lenard","Leo","Lennie","Lemuel","Lennon","Lenora","Lenord","Lena","Lenna","Lenny","Lenon","Lenore","Leola","Leonia","Leonor","Lera","Leonidas","Leon","Leona","Leonore","Leonce","Leoma","Leone","Leonie","Les","Leonard","Lesa","Leontine","Leroy","Leonardo","Leopold","Lenwood","Leslee","Leonel","Leonora","Leopoldo","Leora","Leota","Lesley","Lesia","Lesli","Letha","Lesly","Less","Lessie","Lester","Lesta","Leslie","Levi","Letitia","Letta","Levy","Levern","Leta","Leticia","Leva","Lethia","Levie","Levar","Letty","Lettie","Levina","Levin","Liller","Libby","Lex","Lillianna","Lewis","Liddie","Liana","Levon","Lida","Lew","Lillard","Leyla","Lexis","Lexie","Lilia","Lidie","Lexi","Lexus","Lilian","Liam","Libbie","Lilla","Liane","Lila","Lidia","Lilah","Lillian","Lillia","Liberty","Lige","Lia","Lilyan","Lilie","Lilburn","Lilly","Lilianna","Liliana","Lilliana","Lillis","Linda","Linzy","Linette","Lily","Lisa","Linna","Linus","Lina","Lillie","Lilyana","Lindbergh","Lindsay","Lindsey","Linden","Link","Lindy","Linton","Linnea","Lincoln","Lim","Linnie","Lisandro","Lindell","Linwood","Linn","Lisette","Lisha","Linsey","Lionel","Lissa","Lissette","Lisbeth","Lise","Lisle","Lish","Lita","Liston","Lissie","Litha","Liza","Little","Livia","Lizabeth","Littie","Littleton","Litzy","Liz","Lizbeth","Loda","Lizzie","Lollie","Lizeth","Lizette","Lloyd","Lois","Logan","Lockie","Llewellyn","Loma","Lolla","Lola","Lon","Lona","Lonny","Loney","Londyn","Lonzo","Long","London","Lolita","Lorelai","Lone","Lora","Loran","Lonna","Lonnie","Lorean","Loree","Loni","Lonie","Lorenz","Loreen","Lorene","Lorelei","Loren","Lorena","Loraine","Lorayne","Loria","Lorraine","Loriann","Loretto","Lorenzo","Lori","Lorine","Lorrie","Lorie","Loretta","Lorna","Lorinda","Lorin","Lorne","Lorrayne","Lorri","Lorenza","Lossie","Loris","Loring","Lott","Louanna","Lotta","Lota","Louetta","Louie","Louisa","Lottie","Lou","Louella","Loy","Lovina","Loyce","Louann","Louisiana","Louvenia","Lovell","Louise","Loula","Lovie","Lovey","Louis","Loyal","Lowell","Lovett","Lu","Lovisa","Love","Lourdes","Loyd","Luana","Luciano","Ludie","Lucetta","Luberta","Luanne","Lucie","Lucero","Lucas","Lugenia","Luc","Luca","Lucina","Luisa","Luann","Luciana","Lucian","Lucille","Lucy","Lue","Luetta","Lula","Luda","Lucia","Lucius","Lucien","Luigi","Luis","Ludwig","Lulie","Lucinda","Lucindy","Lucio","Lucretia","Lucile","Lukas","Luke","Lucky","Lucious","Luka","Luella","Lular","Lulah","Lynnette","Lum","Lurana","Lynda","Lute","Luster","Lyndsay","Luna","Lulla","Lura","Lupe","Lydell","Lurena","Luvinia","Lulu","Lynette","Luz","Lutie","Lydia","Lyman","Lyla","Lurline","Luverne","Lynsey","Luther","Lyda","Lyle","Lyndia","Lynwood","Lyndsey","Lyndon","Luvenia","Lyn","Lynn","Lynne","Lyric","Nallely","Nakisha","Nannette","Nanie","Nadine","Nataly","Naima","Nadia","Napoleon","Naoma","Nada","Nash","Narcissus","Natalee","Natosha","Newman","Nayeli","Najee","Nanna","Nayely","Nelson","Nedra","Nick","Neil","Nan","Newell","Neely","Nathen","Neha","Nickolas","Nico","Nanette","Nathaly","Naomi","Nathan","Nelly","Nancie","Nelia","Nathalia","Nery","Netta","Newt","Namon","Nannie","Nealie","Nehemiah","Neta","Nicolette","Nia","Nichelle","Nana","Nathaniel","Nakita","Ned","Nakia","Natalie","Natalia","Nicholas","Nelda","Nasir","Nestor","Neal","Nathanial","Nevada","Nevaeh","Newton","Nicky","Nels","Nicole","Nettie","Nanci","Nat","Nicklaus","Natalya","Nicholaus","Nelle","Neoma","Nancy","Nathalie","Natasha","Needham","Nello","Nichole","Nelie","Neveah","Nella","Nellie","Nautica","Nathanael","Neola","Neppie","Nena","Nicki","Nealy","Nereida","Neva","Nell","Nicola","Nevin","Nichol","Nicolas","Nicolle","Nikhil","Nila","Niko","Nikolas","Niles","Nigel","Nikki","Niki","Nikole","Nikita","Nikolai","Nikko","Nikia","Nilda","Nim","Nile","Nola","Norbert","Nils","Noel","Nohely","Noemi","Nita","Nobie","Nonie","Nira","Nolie","Nina","Noelia","Noma","Ninnie","Noble","Nora","Nolen","Nolan","Nolia","Noemie","Nona","Noah","Noe","Noelle","Norton","Norma","Norita","Norah","North","Norman","Norberto","Noretta","Noreta","Noreen","Norwood","Novella","Norene","Normand","Norine","Norris","Nyah","Nunzio","Nya","Norval","Nova","Nylah","Nyasia","Nyla","Nyree","Hamilton","Halle","Hali","Haley","Hakeem","Halley","Hailee","Hannah","Halie","Haruko","Hadassah","Hailie","Harrold","Harlen","Hansford","Handy","Hadley","Halbert","Harm","Harriette","Haiden","Hale","Hartwell","Haylie","Harmon","Harvey","Hailey","Hal","Hall","Harvy","Hans","Hassie","Hanson","Haskell","Hasan","Halsey","Harley","Harlan","Harding","Harman","Harlene","Hector","Harlow","Ham","Hanna","Hampton","Hakim","Hardin","Hansel","Hamp","Haleigh","Hana","Harve","Harrison","Harry","Heath","Hardie","Hazel","Heber","Hart","Haywood","Harlon","Hartley","Hallie","Hank","Harmony","Heather","Harland","Hayleigh","Harriett","Hayes","Hamza","Haden","Harden","Harl","Harper","Hasel","Hays","Haven","Hazle","Harrie","Hassan","Harrell","Hardy","Harold","Heaven","Harris","Hazelle","Harriet","Hattie","Hazen","Harlie","Hayden","Hedy","Hebert","Hayward","Haylee","Hedwig","Harvie","Hayley","Heidi","Helaine","Helyn","Hennie","Hence","Henderson","Henretta","Helga","Helene","Helma","Hellen","Heidy","Henery","Helena","Helen","Helmer","Herb","Henry","Henriette","Herma","Herlinda","Herbert","Henrietta","Hermine","Hermann","Henri","Herman","Heriberto","Hershell","Herminia","Hermon","Hertha","Hershel","Hilary","Hilario","Hernan","Hezekiah","Hilah","Hester","Hervey","Hettie","Hillary","Hezzie","Hetty","Holden","Hillard","Herschel","Hermina","Heyward","Hilton","Hilma","Hiroshi","Hildred","Hoke","Hilbert","Hildur","Hillery","Hill","Hideo","Hobart","Hjalmer","Hilliard","Hiram","Hessie","Hildegarde","Hobson","Hildegard","Hollis","Hjalmar","Hilmer","Hilda","Holly","Hobert","Horatio","Hosie","Holland","Hoyt","Hortencia","Holli","Holmes","Hubbard","Hope","Horace","Hollie","Hoover","Honora","Hortensia","Hortense","Horacio","Horton","Homer","Howard","Hosteen","Hoy","Hugh","Howell","Hosea","Hudson","Huey","Houston","Hubert","Hughes","Hung","Hulda","Huston","Hughie","Hunt","Huldah","Hugo","Humphrey","Hughey","Hyman","Huy","Humberto","Hunter","Hurley","Hurbert","Hyrum","Hymen","Walton","Waino","Wava","Washington","Waymon","Wilhelmina","Wilhelmine","Whitney","Wenzel","Waneta","Wilbur","Walker","Werner","Wardell","Weldon","Wanita","Waldo","Ward","Welton","Wade","Weaver","Watson","Wilkie","Webb","Wayman","Wanda","Wells","Wilford","Wesley","Wilton","Wilburn","Webster","Watt","Wayland","Wallace","Willaim","Wally","Waldemar","Willa","West","Wiley","Williams","Willard","Winston","Wayne","Waverly","Wayde","Warren","Wellington","Wash","Wende","Wilda","Waylon","Warner","Wilbert","Williemae","Weston","Wilfredo","Wilfrid","Will","Whit","Winifred","Winfield","Windy","Wing","Walter","Wendel","Wendell","Wes","Walt","Wheeler","Wendi","Wilfred","Willie","Winfred","Willow","Westley","Willodean","Willam","Wilson","Wilmer","Winford","Wilber","Willian","Williard","Willis","Winona","Winter","Wiliam","Wilma","Wendy","Willene","Wess","Whitley","Wilhelm","Willy","Winnie","Willia","Winnifred","Windell","William","Winthrop","Winton","Wong","Woodie","Wirt","Wright","Wm","Worth","Wood","Woodson","Woody","Worley","Woodroe","Wynona","Woodrow","Wyman","Wyatt","Wylie","Kaeden","Kaleena","Kadeem","Kade","Kaelyn","Kaaren","Karol","Kailyn","Kaela","Kari","Kalie","Kamari","Kael","Kandi","Kaitlyn","Kaitlynn","Kandice","Kanesha","Kaia","Kailee","Kaci","Kaitlin","Kadijah","Kaleb","Kaiden","Kameron","Kareen","Karan","Kallie","Kamron","Kaley","Karley","Kareem","Kalyn","Kadence","Kahlil","Kacey","Karson","Kala","Karyme","Karen","Karma","Kandace","Kailey","Kadin","Kalen","Kaliyah","Kamden","Karlene","Kandy","Kanisha","Kacie","Karla","Karina","Kacy","Kadyn","Kamren","Kaleigh","Kaila","Kalvin","Kai","Kamilah","Karl","Kamila","Kaden","Kaiya","Karren","Karli","Kanye","Karis","Karolyn","Karon","Kale","Kali","Kara","Karlie","Karel","Kalene","Kami","Kamryn","Kamora","Karly","Karin","Kasie","Karissa","Karim","Karri","Karyl","Karlee","Kason","Kasey","Karie","Karyn","Kane","Kassandra","Kasen","Kash","Karsyn","Karrie","Kasandra","Karter","Kate","Kassidy","Kassie","Kathleen","Katherin","Katelyn","Katharina","Kathaleen","Katherine","Katarina","Kathie","Katharine","Kathryn","Katelin","Katharyn","Katelynn","Kathern","Katheryn","Kathrine","Katerina","Kathey","Kathi","Kathlene","Katina","Kati","Katlyn","Kathlyn","Kathy","Kathryne","Katie","Katrina","Kathyrn","Katia","Katlynn","Kattie","Kavon","Kaya","Katlin","Kayden","Kaycee","Kaylyn","Kaye","Kayleigh","Keagan","Kay","Kaydence","Kaylan","Kaylen","Kayla","Kaylee","Katy","Kayli","Kaylah","Kaylin","Kaylene","Kaylie","Keanna","Keegan","Kayley","Kecia","Keaton","Keena","Kazuko","Kaylynn","Keandre","Kazuo","Keifer","Keanu","Keeley","Keira","Keesha","Keila","Keara","Keely","Keenan","Keith","Kelly","Kelsey","Kelcie","Kellie","Kellee","Kem","Kelley","Keisha","Kelis","Kellan","Kelli","Kegan","Kellen","Kelby","Keli","Keion","Kelan","Kelsea","Kelsi","Kelton","Kelsie","Keenen","Kelvin","Ken","Kenan","Kenia","Kendrick","Kendra","Kenji","Kendal","Kendell","Kenley","Kenisha","Kendall","Kennedi","Kent","Kenzie","Kenny","Kenna","Kennedy","Keri","Kennard","Kesha","Keon","Kenneth","Kennth","Kerrie","Kenyatta","Kenney","Kenyon","Kenton","Kenya","Kennith","Kerri","Kermit","Keshawn","Kerry","Keshaun","Khiry","Keshia","Kiana","Khari","Keyon","Kevon","Khalid","Khalil","Kerwin","Keven","Kevin","Keyshawn","Kiara","Khadijah","Kia","Kevan","Khloe","Khalilah","Keyla","Kianna","Kit","Kiel","Kindra","Kieth","Kian","Kiera","Kip","Kimber","Kimberlee","Kiefer","Kiarra","Kim","Kimberely","Kierra","Kimberlie","Killian","Kiley","Kinsey","Kieran","Kimberley","Kimora","King","Kimball","Kimberly","Kingston","Kirby","Kinley","Kiersten","Kirstie","Kinsley","Kimberli","Kira","Kirsten","Kipp","Kizzie","Kizzy","Kitty","Kirt","Kinte","Kirstin","Kisha","Kiyoshi","Kittie","Kiyoko","Kirk","Kiya","Knox","Kobe","Koby","Knute","Konner","Kolton","Kraig","Kody","Konnor","Krissy","Koda","Kolten","Kolby","Korbin","Kole","Kori","Kortney","Kordell","Krista","Korey","Krish","Koen","Kory","Kourtney","Krystle","Kristin","Kristen","Kristian","Krystal","Kristine","Kristi","Kya","Krystin","Kristal","Kris","Kylah","Krysten","Kwame","Kristofer","Kristie","Kristoffer","Kristan","Kristina","Kunta","Kristyn","Krysta","Kristopher","Kurtis","Kristy","Kurt","Krystina","Kyrie","Kyan","Kyle","Kylee","Kyara","Kylene","Kylan","Kyleigh","Kylie","Kyree","Kyla","Kyler","Kyra","Kymani","Kyson","Jacquline","Jackeline","Jabbar","Jabari","Jaelynn","Jack","Jacki","Jacklyn","Jaiden","Jackson","Jaimie","Jacquelyn","Jadyn","Jaeda","Jailyn","Jacquelynn","Jaime","Jake","Jaden","Jameson","Jacques","Jahiem","Jada","Jaimee","Janessa","Jame","Jamari","Jacque","Jaclyn","Jamarcus","Jacquez","Jailene","Jalyn","Jair","Jacquelin","Jaheim","Jaeden","Jakob","Jagger","Jaliyah","Jamir","Jabez","Jamey","Jammie","Jamel","Jacob","Jahir","Jaidyn","Jaleesa","Jacqulyn","Jaida","Jackie","Jairo","Jacqueline","Jalisa","Jaleel","Jakobe","Jami","Jameel","Jamil","James","Jaelyn","Jaheem","Jalissa","Janeen","Jalynn","Janell","Jamiya","Jamie","Jamaal","Jamila","Janey","Janelle","Jamar","Jade","Jacoby","Jace","Jacey","Jajuan","Jakayla","Jacky","Jamya","Jamal","Jadiel","Jacalyn","Jamarion","Jan","Janet","Jamison","Jalon","Jadon","Jana","Jane","Janay","Janette","Janene","Jamin","Janiah","Jalen","Janel","Janae","Janine","Janie","Janiya","Janice","Janis","Janna","Janiyah","Jannie","Jannette","Jann","Jaquelin","Jaquan","Janyce","January","Jaqueline","Jaquez","Jarad","Jaret","Jared","Jaren","Jarrad","Jarred","Jaron","Jarett","Jarod","Jarvis","Jarrett","Jarrod","Jarret","Jarrell","Jasiah","Jasen","Jaslene","Jaslyn","Jasmyn","Jase","Jason","Jasmine","Jasmin","Jasmyne","Javen","Jax","Jasper","Javier","Jaunita","Javion","Javon","Jaxson","Jaxon","Jaycee","Jay","Javonte","Jayden","Jaycie","Jayce","Jayda","Jaydan","Jaydin","Jayde","Jaye","Jaydon","Jaylah","Jaylee","Jayla","Jaylan","Jayleen","Jaylene","Jaylon","Jayme","Jaylyn","Jaylin","Jaylen","Jayne","Jayson","Jayvon","Jazlyn","Jaymes","Jaylynn","Jazmine","Jazlynn","Jazlene","Jazmyne","Jayvion","Jazmyn","Jeanette","Jean","Jeane","Jazmin","Jeanne","Jeanmarie","Jeana","Jeanetta","Jeanie","Jeanine","Jeanna","Jed","Jeannine","Jeannette","Jeb","Jeannie","Jedediah","Jeff","Jefferson","Jedidiah","Jefferey","Jeffrey","Jeffery","Jeffie","Jeffry","Jelani","Jena","Jenifer","Jemal","Jemima","Jenelle","Jenni","Jennette","Jennie","Jenna","Jenilee","Jennifer","Jennings","Jenny","Jerad","Jens","Jenniffer","Jep","Jensen","Jeptha","Jerald","Jeramie","Jeramiah","Jeraldine","Jeramy","Jere","Jerel","Jereme","Jeremey","Jered","Jeremiah","Jeremie","Jeri","Jeremy","Jerilyn","Jerica","Jerilynn","Jermey","Jeromy","Jerome","Jermaine","Jerimy","Jerold","Jerod","Jerimiah","Jermain","Jerri","Jerrica","Jerrell","Jerrad","Jerrel","Jerrold","Jerry","Jerrod","Jerrie","Jerrilyn","Jerusha","Jeryl","Jess","Jesica","Jesenia","Jesse","Jessica","Jessenia","Jessee","Jessi","Jessy","Jesus","Jessye","Jessika","Jessie","Jethro","Jett","Jettie","Jevon","Jetta","Jewell","Jillian","Jewel","Jill","Jiles","Jimena","Jim","Jinnie","Jimmie","Jimmy","Jo","Joana","Joanie","Joan","Joann","Joanna","Job","Joaquin","Joanne","Jobe","Jocelynn","Jodi","Jocelyne","Jocelyn","Jodie","Joel","Joe","Joella","Joell","Jody","Joelle","Joesph","Joellen","Joeseph","Joetta","Johanna","Joey","Joette","Johana","Johan","Johnathan","Johannah","John","Johnathon","Johathan","Johney","Johnie","Johnny","Johnpaul","Johnnie","Johnna","Johnson","Joi","Johny","Joleen","Jolene","Jolette","Jon","Joline","Jolie","Jonathon","Jonah","Jonathan","Jonell","Jonas","Jonnie","Jonatan","Jordan","Jones","Jordon","Jonna","Joni","Jordy","Jordi","Jorden","Jorja","Jordin","Jory","Jordyn","Josefina","Joretta","Josefita","Jorge","Joselyn","Josef","Joseph","Josefa","Jose","Joselin","Joseluis","Joshua","Josh","Joseline","Josiephine","Josephus","Josephine","Joslyn","Josette","Jovani","Josiah","Joshuah","Jovan","Josie","Jossie","Josue","Jovita","Journey","Joy","Jovanny","Jovanni","Juan","Jovany","Joycelyn","Joyce","Joye","Judah","Judd","Juana","Juanita","Jude","Judi","Judson","Judie","Judge","Judith","Judy","Juli","Judyth","Jules","Jule","Juliann","Juliana","Julian","Julia","Julianna","Juliet","Juliette","Julie","Julien","Julianne","Julious","Julius","Julissa","Julio","Junior","Juluis","Junie","Julisa","June","Junia","Junious","Junius","Justice","Justin","Justina","Juston","Justen","Justus","Juwan","Justyn","Justine","Xander","Xiomara","Xzavier","Xena","Xavier","Ximena","Yaakov","Yadiel","Yandel","Yahaira","York","Yamilet","Yamilex","Yareli","Yaritza","Yael","Yolanda","Yancy","Yoel","Yahir","Yasmine","Yasmin","Yessenia","Yetta","Yuridia","Yasmeen","Yesenia","Yurem","Yaretzi","Yehuda","Yadira","Yazmin","Yair","Yuliana","Yoshio","Yolonda","Yee","Yvonne","Young","Yosef","Yajaira","Yoselin","Yulissa","Yoshiko","Yvette","Yulisa","Yusuf"};
        String[] countries = {"Afghanistan","Albania","Algeria","Andorra","Angola","Anguilla","Antigua-Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bosnia-Herzegovina","Botswana","Brazil","British Virgin Islands","Brunei","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Cape Verde","Cayman Islands","Chad","Chile","China","Colombia","Congo","Cook Islands","Costa Rica","Cote D Ivoire","Croatia","Cruise Ship","Cuba","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Estonia","Ethiopia","Falkland Islands","Faroe Islands","Fiji","Finland","France","French Polynesia","French West Indies","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guam","Guatemala","Guernsey","Guinea","Guinea Bissau","Guyana","Haiti","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Isle of Man","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kuwait","Kyrgyz Republic","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macau","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Mauritania","Mauritius","Mexico","Moldova","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Namibia","Nepal","Netherlands","Netherlands Antilles","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","Norway","Oman","Pakistan","Palestine","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal","Puerto Rico","Qatar","Reunion","Romania","Russia","Rwanda","Saint Pierre-Miquelon","Samoa","San Marino","Satellite","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia","Slovenia","South Africa","South Korea","Spain","Sri Lanka","St Kitts-Nevis","St Lucia","St Vincent","St. Lucia","Sudan","Suriname","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Timor L'Este","Togo","Tonga","Trinidad-Tobago","Tunisia","Turkey","Turkmenistan","Turks-Caicos","Uganda","Ukraine","United Arab Emirates","United Kingdom","Uruguay","Uzbekistan","Venezuela","Vietnam","Virgin Islands (US)","Yemen","Zambia","Zimbabwe"};


        for(int i = 0; i < howManyUsers; i++){
            String fName = firstName[generator.nextInt(firstName.length)];
            System.out.println("NOME ====================>>>>>>>" + fName);
            String lName = lastName[generator.nextInt(lastName.length)];
            int age = generator.nextInt(57) + 18;
            String username;
            String password = "";
            for(int j = 0; j < 6; j++){
                password += (char) (generator.nextInt(26) + 'a');
            }
            password = Integer.toString(generator.nextInt(100));
            String country = countries[generator.nextInt(countries.length)];

            try {
                username = fName.substring(0, 3) + lName.substring(0, 3) + (Year.now().getValue() - age);
            }catch (IndexOutOfBoundsException index){
                continue;
            }
            User user = new User(username, password,fName, lName, age, country);

            UserDAOImpl userDAO = new UserDAOImpl();

            userDAO.createUser(user);
            System.out.println("-------------------------------RANDOM LIKE PLAYER------------------------");

            likeRandomPlayers(user, 5);

        }
        createRandomSquad(20000, 3, 22);
        System.out.println("-------------------------------RANDOM LIKES------------------------");
        completelyRandomLikes(20000);
        System.out.println("-------------------------------RANDOM FOLLOWS SQUAD------------------------");
        completelyRandomSquadFollow(1000);
        completelyRandomUserFollows(50000);



        System.out.println("RESOLVE INCONSISTENCIES");
        //resolveSquadInconsistencies();
        /*
        resolvePlayerInconsistencies();
        resolveUserInconsistencies();
        resolveNode();

         */
    }


    public static MongoCollection<Document> getRandomPlayer() throws ActionNotCompletedException {
        MongoCollection<Document> playersCollection = (MongoCollection<Document>) MongoDriver.getInstance().getCollection(Collections.PLAYERS);
        System.out.println(playersCollection.countDocuments());
        return playersCollection;
    }


    public static Player getRandomAttacker(MongoCollection<Document> playersCollection) throws ActionNotCompletedException{
        Player player = null;

        Bson sample = sample(6000);
        Bson match1 =  match(eq("position", "Centre-Forward"));
        Bson match2 = match(or(eq("league", "LaLiga"),
                eq("league", "Premier League"),
                eq("league", "Serie A"),
                eq("league", "Bundesliga"),
                eq("league", "Ligue 1")));


        try (MongoCursor<Document> cursor = playersCollection.aggregate(Arrays.asList(match1,
                match2,
                sample)).iterator()) {
            if(cursor.hasNext()) {
                player = new Player(cursor.next());
                System.out.println(player.getName());
            }
        }catch (MongoException mongoEx) {
            System.out.println(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return player;
    }

    public static Player getRandomDefender(MongoCollection<Document> playersCollection) throws ActionNotCompletedException{
        Player player = null;

        Bson sample = sample(6000);
        Bson match1 =  match(or(eq("position", "Centre-Back"),
                eq("position", "Left-Back"),
                eq("position", "Right-Back")));
        Bson match2 = match(or(eq("league", "LaLiga"),
                eq("league", "Premier League"),
                eq("league", "Serie A"),
                eq("league", "Bundesliga"),
                eq("league", "Ligue 1")));


        try (MongoCursor<Document> cursor = playersCollection.aggregate(Arrays.asList(match1,
                match2,
                sample)).iterator()) {
            if(cursor.hasNext()) {
                player = new Player(cursor.next());
                System.out.println(player.getName());

            }
        }catch (MongoException mongoEx) {
            System.out.println(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return player;
    }

    public static Player getRandomMidfielder(MongoCollection<Document> playersCollection) throws ActionNotCompletedException{
        Player player = null;

        Bson sample = sample(6000);
        Bson match1 =  match(or(eq("position", "DefensiveMidfield"),
                eq("position", "CentralMidfield"),
                eq("position", "AttackingMidfield"),
                eq("position", "LeftWinger"),
                eq("position", "RightWinger")));
        Bson match2 = match(or(eq("league", "LaLiga"),
                eq("league", "Premier League"),
                eq("league", "Serie A"),
                eq("league", "Bundesliga"),
                eq("league", "Ligue 1")));


        try (MongoCursor<Document> cursor = playersCollection.aggregate(Arrays.asList(match1,
                match2,
                sample)).iterator()) {
            if(cursor.hasNext()) {
                player = new Player(cursor.next());
                System.out.println(player.getName());

            }
        }catch (MongoException mongoEx) {
            System.out.println(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return player;
    }

    public static Player getRandomGoalkeeper(MongoCollection<Document> playersCollection) throws ActionNotCompletedException{
        Player player = null;

        Bson sample = sample(6000);
        Bson match1 =  match(eq("position", "Goalkeeper"));
        Bson match2 = match(or(eq("league", "LaLiga"),
                eq("league", "Premier League"),
                eq("league", "Serie A"),
                eq("league", "Bundesliga"),
                eq("league", "Ligue 1")));


        try (MongoCursor<Document> cursor = playersCollection.aggregate(Arrays.asList(match1,
                match2,
                sample)).iterator()) {
            if(cursor.hasNext()) {
                player = new Player(cursor.next());
                System.out.println(player.getName());
            }
        }catch (MongoException mongoEx) {
            System.out.println(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return player;
    }

    public static User getRandomUser() throws ActionNotCompletedException{
        MongoCollection<Document> usersCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
        User user = null;

        Bson sample = sample(1);

        try (MongoCursor<Document> cursor = usersCollection.aggregate(Arrays.asList(sample)).iterator()) {
            if(cursor.hasNext()) {
                Document result = cursor.next();
                user = new User(result);
                System.out.println("\n\nChoosen Player\n\n");
                System.out.println(user.getUsername());
                System.out.println("\n\nFINE\n\n");

            }
        }catch (MongoException mongoEx) {
            System.out.println(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }

        return user;
    }

    //select some random users and add random number of squad to them, with a random number of random players
    public static void createRandomSquad(int numUsers, int maxNumSquadsPerUser, int maxNumPlayersPerSquads) throws ActionNotCompletedException{
        Random random = new Random();
        //maxNumPlayersPerSquads = 11;
        for (int i = 0; i < numUsers; i++){
            User user = getRandomUser();
            System.out.println(user.getUsername());
            int numSquads = random.nextInt(maxNumSquadsPerUser);
            for (int j = 0; j < numSquads; j++){
                int current_position = random.nextInt(22);
                Squad squad = new Squad(user.getUsername(), "Squad " + (j + 1) + " by " + user.getFirstName());
                squad.setCurrent_position(current_position);
                MongoCollection<Document> playersList = getRandomPlayer(); // MongoDriver.getInstance().getCollection(Collections.PLAYERS);;
                System.out.println(playersList.countDocuments());
                Bson sample = sample(1000);
                Player player = null;
                try (MongoCursor<Document> cursor = playersList.aggregate(Arrays.asList(sample)).iterator()) {
                    if(cursor.hasNext()) {
                        player = new Player(cursor.next());
                    }
                }catch (MongoException mongoEx) {
                    System.out.println(mongoEx.getMessage());
                    throw new ActionNotCompletedException(mongoEx);
                }
                squad.setUrlImage(player.getImageUrl());
                connector.createSquad(squad);
                addRandomPlayers(squad, maxNumPlayersPerSquads);

            }
        }
    }

    public static PlayerDAOImpl addRandomPlayers(Squad squad, int numPlayers) throws ActionNotCompletedException{
        MongoCollection<Document> playersList = getRandomPlayer();
        System.out.println(playersList.countDocuments());
        int i;
        Player player;
        Player defender;
        Player midfielder;
        Player attacker;
        for (i = 0; i < numPlayers; i++){
            if (i <  3) {
                player = getRandomGoalkeeper(playersList);
                System.out.println(player.getName());
                System.out.println(player.getPosition());
                System.out.println("***********************************************++++");
                connector.addPlayer(squad, player);
            }
            else if (2 < i && i < 9) {
                defender = getRandomDefender(playersList);
                System.out.println(defender.getName());
                System.out.println(defender.getPosition());
                System.out.println(defender.getLeague());
                System.out.println("***********************************************++++");
                connector.addPlayer(squad, defender);
            }
            else if (8 < i  && i < 16) {
                midfielder = getRandomMidfielder(playersList);
                System.out.println(midfielder.getName());
                System.out.println(midfielder.getPosition());
                System.out.println("***********************************************++++");
                connector.addPlayer(squad, midfielder);
            }
            else if (15 < i && i < 22) {
                attacker = getRandomAttacker(playersList);
                System.out.println(attacker.getName());
                System.out.println(attacker.getPosition());
                System.out.println("***********************************************++++");
                connector.addPlayer(squad, attacker);
            }
        }
        return null;
    }

    //put some likes to random players from a user
    public static void likeRandomPlayers(User user, int numLikes) throws ActionNotCompletedException{
        UserDAO userDao = new UserDAOImpl();
        MongoCollection<Document> playersList = getRandomPlayer();
        for (int i = 0; i < numLikes; i++)
            if (i < 4)
            userDao.likePlayer(user, getRandomGoalkeeper(playersList));
            else if (i > 4 && i < 10)
                userDao.likePlayer(user, getRandomDefender(playersList));
            else if (i > 4 && i < 20)
                userDao.likePlayer(user, getRandomMidfielder(playersList));
            else if (i > 4 && i < 100)
                userDao.likePlayer(user, getRandomAttacker(playersList));
    }

    //put some likes to random players from random users
    public static void completelyRandomLikes(int numLikes) throws ActionNotCompletedException {
        UserDAO userDao = new UserDAOImpl();
        MongoCollection<Document> playersList = getRandomPlayer();
        for (int i = 0; i < numLikes; i++) {
            if (i < 4)
                userDao.likePlayer(getRandomUser(), getRandomGoalkeeper(playersList));
            else if (i > 4 && i < 10)
                userDao.likePlayer(getRandomUser(), getRandomDefender(playersList));
            else if (i > 4 && i < 20)
                userDao.likePlayer(getRandomUser(), getRandomMidfielder(playersList));
            else if (i > 4 && i < 100)
                userDao.likePlayer(getRandomUser(), getRandomAttacker(playersList));
        }

    }

    public static void completelyRandomUserFollows(int numFollows) throws ActionNotCompletedException {
        UserDAO userDAO = new UserDAOImpl();

        for (int i = 0; i < numFollows; i++) {
            userDAO.followUser(getRandomUser(), getRandomUser());

        }
    }

    public static Squad getRandomSquad() throws NoSuchRecordException {
        Squad squad;
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
         System.out.println(session);

            squad = session.readTransaction(tx -> {
                Result result = tx.run(     "MATCH (p:Squad) \n" +
                        "RETURN p, rand() as r\n" +
                        "ORDER BY r\n" +
                        "LIMIT 1;");
                return new Squad(result.next().get("p"));
            });
        }
        return squad;
    }

    public static void completelyRandomSquadFollow(int numFollow) throws ActionNotCompletedException {
        System.out.println("FOLLOW SQUAD");
        UserDAO userDAO = new UserDAOImpl();
        for (int i = 0; i < numFollow; i++)
            userDAO.followSquad(getRandomUser(), getRandomSquad());
    }

    private static void resolvePlayerInconsistencies() {

        MongoCollection<Document> playerCollection = MongoDriver.getInstance().getCollection(Collections.PLAYERS);
        PlayerDAO playerDAO = new PlayerDAOImpl();
        try (MongoCursor<Document> cursor = playerCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Player mongoPlayer = new Player(cursor.next());
                try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
                    String query = "MATCH (s:Player) " +
                            "WHERE s.playerId = $playerId " +
                            "RETURN s.playerId as playerId " ;
                    Result result = session.run(query, parameters("playerId", mongoPlayer.getID()));

                    if(!result.hasNext()){
                        System.out.println("Sure to delete id: " + mongoPlayer.getID());
                        System.out.print("> ");
                        String response = new Scanner(System.in).nextLine();
                        if(response.equals("yes"))
                            playerDAO.deletePlayerDocument(mongoPlayer);
                        else
                            System.out.println("Not deleted");
                    }
                }
            }
        }
    }

        private static void resolveNode() throws ActionNotCompletedException {
            try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
                Result result = session.run("MATCH (u:User) RETURN u.username AS username");
                UserDAO userDAO = new UserDAOImpl();
                while(result.hasNext()) {
                    System.out.println("O");
                    String username = result.next().get("username").asString();
                    User user = userDAO.getUserByUsername(username);
                    if (user == null) {
                        user = new User(username);
                        //userDAO.deleteUserNode(user);
                        System.out.println("cancellato");
                    }
                }
            }catch (Neo4jException neo4){
                neo4.printStackTrace();
            }
        }

    private static void resolveSquadInconsistencies() throws ActionNotCompletedException {

        MongoCollection<Document> userCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
        System.out.println("COUNT USERS");
        System.out.println(userCollection.countDocuments());
        SquadDAO squadDAO = new SquadDAOImpl();
        try (MongoCursor<Document> cursor = userCollection.find().iterator()) {
            int count = 0;
            while (cursor.hasNext()) {
                ++count;
                System.out.println("\n\n" + count + "\n\n");
                Squad mongoSquad = new Squad(cursor.next());
                System.out.println("\n\n----------------------------------------\n\n");
                System.out.println(mongoSquad.getID());
                try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
                    String query = "MATCH (p:Squad) " +
                            "WHERE p.squadId = $squadId " +
                            "RETURN p.squadId"; //as p.squadId " ;
                    Result result = session.run(query, parameters("squadId", mongoSquad.getID()));
                    System.out.println("RESULT");
                    System.out.println(result);

                    if(!result.hasNext()){
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        System.out.println(mongoSquad.getID());
                        //System.out.println("Sure to delete id: " + mongoSquad.getID());
                        //System.out.print("> yes");
                        //String response = new Scanner(System.in).nextLine();
                        //if(response.equals("yes"))
                        //squadDAO.deleteSquadDocument(mongoSquad);
                        //else
                        //    System.out.println("Not deleted");


                    }
                }
            }
        }
    }

    private static void resolveUserInconsistencies() {

        MongoCollection<Document> playerCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
        UserDAO userDAO = new UserDAOImpl();
        try (MongoCursor<Document> cursor = playerCollection.find().iterator()) {
            while (cursor.hasNext()) {
                User mongoUser = new User(cursor.next());
                try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
                    String query = "MATCH (u:User) " +
                            "WHERE u.username = $username " +
                            "RETURN u.username as username " ;
                    Result result = session.run(query, parameters("username", mongoUser.getUsername()));

                    if(!result.hasNext()){
                        System.out.println("Sure to delete id: " + mongoUser.getUsername());
                        userDAO.deleteUserDocument(mongoUser);
                        System.out.println("deleted");
                    }
                }
            }
        }
    }

}