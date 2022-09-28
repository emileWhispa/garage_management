# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

INSERT INTO user VALUES(0,'','2018-11-02 22:38:19.672000','078978','Amani@jaliholdings.com','Amani','jali');
INSERT INTO role VALUES(0,'','2018-11-02 22:38:19.672000','Administrator','admin');
INSERT INTO role VALUES(0,'','2018-11-02 22:38:19.672000','Garage manager','garageManger');
INSERT INTO role VALUES(0,'','2018-11-02 22:38:19.672000','Stock keeper','storeKeeper');
INSERT INTO role VALUES(0,'','2018-11-02 22:38:19.672000','Transport MD','transportMD');
INSERT INTO role VALUES(0,'','2018-11-02 22:38:19.672000','Procurement manager','procurementM');
INSERT INTO role VALUES(0,'','2018-11-02 22:38:19.672000','Chief mechanic','chiefMechanic');
INSERT INTO role VALUES(0,'','2018-11-02 22:38:19.672000','Fore man','foreMan');
INSERT INTO role VALUES(0,'','2018-11-02 22:38:19.672000','Executive Chairman','chairMan');
INSERT INTO role VALUES(0,'','2018-11-02 22:38:19.672000','Finance manager(CFO)','finance');
INSERT INTO role VALUES(0,'','2018-11-02 22:38:19.672000','Accountant','accountant');
INSERT INTO user_role VALUES(0,'','2018-11-02 22:38:19.672000',1,1,true);


# --- !Downs

