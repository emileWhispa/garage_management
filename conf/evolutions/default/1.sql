# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table authorization (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  car_id                        bigint,
  last_authorization            datetime(6),
  next_authorization            datetime(6),
  document                      varchar(255),
  constraint pk_authorization primary key (id)
);

create table brand (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  brand_name                    varchar(255),
  details                       varchar(255),
  constraint pk_brand primary key (id)
);

create table brand_category (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  brand_id                      bigint,
  name                          varchar(255),
  constraint pk_brand_category primary key (id)
);

create table budget (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  spare_id                      bigint,
  amount                        double not null,
  year                          integer not null,
  constraint pk_budget primary key (id)
);

create table car (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  brand_id                      bigint,
  owner_id                      bigint,
  driver_id                     bigint,
  other_id                      bigint,
  car_name                      varchar(255),
  plate_number                  varchar(255),
  yellow_card                   varchar(255),
  detail                        varchar(255),
  constraint pk_car primary key (id)
);

create table control_technique (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  car_id                        bigint,
  last_visit                    datetime(6),
  next_visit                    datetime(6),
  document                      varchar(255),
  constraint pk_control_technique primary key (id)
);

create table driver (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  name                          varchar(255),
  email                         varchar(255),
  phone                         varchar(255),
  id_number                     varchar(255),
  category                      varchar(255),
  appointment_date              datetime(6),
  constraint pk_driver primary key (id)
);

create table driver_activity (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  car_id                        bigint,
  zone_id                       bigint,
  number                        integer not null,
  transport_revenue             double not null,
  fuel                          double not null,
  constraint pk_driver_activity primary key (id)
);

create table info (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  is_old_allowed                tinyint(1) default 0 not null,
  email                         varchar(255),
  address                       varchar(255),
  phone                         varchar(255),
  constraint pk_info primary key (id)
);

create table insurance (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  car_id                        bigint,
  document                      varchar(255),
  company                       varchar(255),
  last_visit                    datetime(6),
  next_visit                    datetime(6),
  cost                          double not null,
  accident_type                 varchar(255),
  accident_date                 datetime(6),
  constraint pk_insurance primary key (id)
);

create table mechanic (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  name                          varchar(255),
  email                         varchar(255),
  phone                         varchar(255),
  constraint pk_mechanic primary key (id)
);

create table old_spare (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  stock_id                      bigint,
  car_id                        bigint,
  mechanic_id                   bigint,
  e_mechanic                    varchar(255),
  new_stock_id                  bigint,
  request_id                    bigint,
  g_approved                    tinyint(1) default 0 not null,
  approved                      tinyint(1) default 0 not null,
  constraint uq_old_spare_stock_id unique (stock_id),
  constraint uq_old_spare_new_stock_id unique (new_stock_id),
  constraint pk_old_spare primary key (id)
);

create table old_spare_request (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  spare_id                      bigint,
  serial_number                 varchar(255),
  quantity                      integer not null,
  chief_mechanic_approval       tinyint(1) default 0 not null,
  fore_man_approval             tinyint(1) default 0 not null,
  fore_man_comment              varchar(255),
  g_manager_approval            tinyint(1) default 0 not null,
  g_manager_comment             varchar(255),
  store_approval                tinyint(1) default 0 not null,
  store_comment                 varchar(255),
  constraint pk_old_spare_request primary key (id)
);

create table private (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  name                          varchar(255),
  address                       varchar(255),
  phone                         varchar(255),
  constraint pk_private primary key (id)
);

create table role (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  role_name                     varchar(255),
  session_name                  varchar(255),
  constraint pk_role primary key (id)
);

create table spare (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  brand_id                      bigint,
  spare_name                    varchar(255),
  spare_detail                  varchar(255),
  price                         double not null,
  constraint pk_spare primary key (id)
);

create table spare_request (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  spare_id                      bigint,
  quantity                      integer not null,
  procurement_mode              tinyint(1) default 0 not null,
  price                         double not null,
  garage_mapproved              tinyint(1) default 0 not null,
  garage_mcomment               varchar(255),
  director_transport            tinyint(1) default 0 not null,
  director_comment              varchar(255),
  procurement_approved          tinyint(1) default 0 not null,
  procurement_comment           varchar(255),
  finance_approved              tinyint(1) default 0 not null,
  finance_comment               varchar(255),
  store_approved                tinyint(1) default 0 not null,
  constraint pk_spare_request primary key (id)
);

create table stock (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  spare_id                      bigint,
  serial_number                 varchar(255),
  supplier_name                 varchar(255),
  price                         double not null,
  time_out                      datetime(6),
  constraint pk_stock primary key (id)
);

create table user (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  phone                         varchar(255),
  email                         varchar(255),
  username                      varchar(255),
  password                      varchar(255),
  constraint pk_user primary key (id)
);

create table user_role (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  role_id                       bigint,
  user_id                       bigint,
  status                        tinyint(1) default 0 not null,
  constraint pk_user_role primary key (id)
);

create table zone (
  id                            bigint auto_increment not null,
  done_by                       varchar(255),
  date                          datetime(6),
  name                          varchar(255),
  route                         varchar(255),
  zone_symbol                   varchar(255),
  target                        double not null,
  constraint pk_zone primary key (id)
);

alter table authorization add constraint fk_authorization_car_id foreign key (car_id) references car (id) on delete restrict on update restrict;
create index ix_authorization_car_id on authorization (car_id);

alter table brand_category add constraint fk_brand_category_brand_id foreign key (brand_id) references brand (id) on delete restrict on update restrict;
create index ix_brand_category_brand_id on brand_category (brand_id);

alter table budget add constraint fk_budget_spare_id foreign key (spare_id) references spare (id) on delete restrict on update restrict;
create index ix_budget_spare_id on budget (spare_id);

alter table car add constraint fk_car_brand_id foreign key (brand_id) references brand_category (id) on delete restrict on update restrict;
create index ix_car_brand_id on car (brand_id);

alter table car add constraint fk_car_owner_id foreign key (owner_id) references private (id) on delete restrict on update restrict;
create index ix_car_owner_id on car (owner_id);

alter table car add constraint fk_car_driver_id foreign key (driver_id) references driver (id) on delete restrict on update restrict;
create index ix_car_driver_id on car (driver_id);

alter table car add constraint fk_car_other_id foreign key (other_id) references driver (id) on delete restrict on update restrict;
create index ix_car_other_id on car (other_id);

alter table control_technique add constraint fk_control_technique_car_id foreign key (car_id) references car (id) on delete restrict on update restrict;
create index ix_control_technique_car_id on control_technique (car_id);

alter table driver_activity add constraint fk_driver_activity_car_id foreign key (car_id) references car (id) on delete restrict on update restrict;
create index ix_driver_activity_car_id on driver_activity (car_id);

alter table driver_activity add constraint fk_driver_activity_zone_id foreign key (zone_id) references zone (id) on delete restrict on update restrict;
create index ix_driver_activity_zone_id on driver_activity (zone_id);

alter table insurance add constraint fk_insurance_car_id foreign key (car_id) references car (id) on delete restrict on update restrict;
create index ix_insurance_car_id on insurance (car_id);

alter table old_spare add constraint fk_old_spare_stock_id foreign key (stock_id) references stock (id) on delete restrict on update restrict;

alter table old_spare add constraint fk_old_spare_car_id foreign key (car_id) references car (id) on delete restrict on update restrict;
create index ix_old_spare_car_id on old_spare (car_id);

alter table old_spare add constraint fk_old_spare_mechanic_id foreign key (mechanic_id) references mechanic (id) on delete restrict on update restrict;
create index ix_old_spare_mechanic_id on old_spare (mechanic_id);

alter table old_spare add constraint fk_old_spare_new_stock_id foreign key (new_stock_id) references stock (id) on delete restrict on update restrict;

alter table old_spare add constraint fk_old_spare_request_id foreign key (request_id) references old_spare_request (id) on delete restrict on update restrict;
create index ix_old_spare_request_id on old_spare (request_id);

alter table old_spare_request add constraint fk_old_spare_request_spare_id foreign key (spare_id) references spare (id) on delete restrict on update restrict;
create index ix_old_spare_request_spare_id on old_spare_request (spare_id);

alter table spare add constraint fk_spare_brand_id foreign key (brand_id) references brand_category (id) on delete restrict on update restrict;
create index ix_spare_brand_id on spare (brand_id);

alter table spare_request add constraint fk_spare_request_spare_id foreign key (spare_id) references spare (id) on delete restrict on update restrict;
create index ix_spare_request_spare_id on spare_request (spare_id);

alter table stock add constraint fk_stock_spare_id foreign key (spare_id) references spare (id) on delete restrict on update restrict;
create index ix_stock_spare_id on stock (spare_id);

alter table user_role add constraint fk_user_role_role_id foreign key (role_id) references role (id) on delete restrict on update restrict;
create index ix_user_role_role_id on user_role (role_id);

alter table user_role add constraint fk_user_role_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_role_user_id on user_role (user_id);


# --- !Downs

alter table authorization drop foreign key fk_authorization_car_id;
drop index ix_authorization_car_id on authorization;

alter table brand_category drop foreign key fk_brand_category_brand_id;
drop index ix_brand_category_brand_id on brand_category;

alter table budget drop foreign key fk_budget_spare_id;
drop index ix_budget_spare_id on budget;

alter table car drop foreign key fk_car_brand_id;
drop index ix_car_brand_id on car;

alter table car drop foreign key fk_car_owner_id;
drop index ix_car_owner_id on car;

alter table car drop foreign key fk_car_driver_id;
drop index ix_car_driver_id on car;

alter table car drop foreign key fk_car_other_id;
drop index ix_car_other_id on car;

alter table control_technique drop foreign key fk_control_technique_car_id;
drop index ix_control_technique_car_id on control_technique;

alter table driver_activity drop foreign key fk_driver_activity_car_id;
drop index ix_driver_activity_car_id on driver_activity;

alter table driver_activity drop foreign key fk_driver_activity_zone_id;
drop index ix_driver_activity_zone_id on driver_activity;

alter table insurance drop foreign key fk_insurance_car_id;
drop index ix_insurance_car_id on insurance;

alter table old_spare drop foreign key fk_old_spare_stock_id;

alter table old_spare drop foreign key fk_old_spare_car_id;
drop index ix_old_spare_car_id on old_spare;

alter table old_spare drop foreign key fk_old_spare_mechanic_id;
drop index ix_old_spare_mechanic_id on old_spare;

alter table old_spare drop foreign key fk_old_spare_new_stock_id;

alter table old_spare drop foreign key fk_old_spare_request_id;
drop index ix_old_spare_request_id on old_spare;

alter table old_spare_request drop foreign key fk_old_spare_request_spare_id;
drop index ix_old_spare_request_spare_id on old_spare_request;

alter table spare drop foreign key fk_spare_brand_id;
drop index ix_spare_brand_id on spare;

alter table spare_request drop foreign key fk_spare_request_spare_id;
drop index ix_spare_request_spare_id on spare_request;

alter table stock drop foreign key fk_stock_spare_id;
drop index ix_stock_spare_id on stock;

alter table user_role drop foreign key fk_user_role_role_id;
drop index ix_user_role_role_id on user_role;

alter table user_role drop foreign key fk_user_role_user_id;
drop index ix_user_role_user_id on user_role;

drop table if exists authorization;

drop table if exists brand;

drop table if exists brand_category;

drop table if exists budget;

drop table if exists car;

drop table if exists control_technique;

drop table if exists driver;

drop table if exists driver_activity;

drop table if exists info;

drop table if exists insurance;

drop table if exists mechanic;

drop table if exists old_spare;

drop table if exists old_spare_request;

drop table if exists private;

drop table if exists role;

drop table if exists spare;

drop table if exists spare_request;

drop table if exists stock;

drop table if exists user;

drop table if exists user_role;

drop table if exists zone;

