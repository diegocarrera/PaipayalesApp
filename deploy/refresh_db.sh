psql << EOF
drop DATABASE paipay_db;
CREATE DATABASE paipay_db;
GRANT ALL PRIVILEGES ON DATABASE paipay_db TO paipay; 
\c paipay_db; 
CREATE EXTENSION postgis; CREATE EXTENSION postgis_topology;
\q
EOF
#sudo -u postgres  ./refresh_db.sh


