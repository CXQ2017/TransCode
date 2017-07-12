# !/bin/sh
#create work dir
p="${HOME}/Hu/test2"
if [ ! -d "$p" ];  then
    mkdir $p
fi

#unpack tars to destination
packs=$(ls ./*.tar)
for t in ${packs} 
    do
    tar -xvf ./$t -C $p
done

#create start and stop scripts in the destination path
des=$(ls -F "$p"|grep /)
for d in $des 
    do
        f=$p/$d"start.sh"
        echo '#!/bin/sh'>$f
        echo "if [ ! \"$p/$d\" = "'"$(pwd)/" ]'>>$f
        echo 'then'>>$f
        echo '  echo "please excute this srcipt in the current path"'>>$f
        echo "  exit">>$f
        echo 'fi'>>$f
        echo 'java -jar ./*.jar &'>>$f
        echo 'echo $! > ./Trans.pid'>>$f
        chmod +x $f

        f=$p/$d"stop.sh"
        echo '#!/bin/sh'>$f
        echo "if [ ! \"$p/$d\" = "'"$(pwd)/" ]'>>$f
        echo 'then'>>$f
        echo '  echo "please excute this srcipt in the current path"'>>$f
        echo "  exit">>$f
        echo 'fi'>>$f
        echo 'PID=$(cat ./Trans.pid)'>>$f 
        echo 'kill -9 $PID'>>$f
        chmod +x $f
done



#create startAll and stopAll scripts
f=$p"/startAll.sh"
echo '#!/bin/sh'>$f
echo 'for d in $(ls -F ./ |grep /)'>>$f
echo 'do'>>$f
echo '  cd $d'>>$f
echo '  ./start.sh'>>$f  
echo '  cd ..'>>$f
echo 'done'>>$f
chmod +x $f
     
f=$p"/stopAll.sh"
echo '#!/bin/sh'>$f
echo 'for d in $(ls -F ./|grep /)'>>$f
echo 'do'>>$f
echo '  cd $d'>>$f
echo '  ./stop.sh'>>$f
echo '  cd ..'>>$f
echo 'done'>>$f 
chmod +x $f  
