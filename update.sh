
root=$PWD

cd $1

cp $root/SampleB_7_1_箱子相互碰撞/build.gradle .
 
cp $root/SampleB_7_1_箱子相互碰撞/gradle.properties .

cp $root/SampleB_7_1_箱子相互碰撞/.gitignore .

cp $root/SampleB_7_1_箱子相互碰撞/app/.gitignore  app

cp $root/SampleB_7_1_箱子相互碰撞/gradle/wrapper/ gradle/ -a


find . -name *.sh | xargs -i  sh -c "mv {} {}_temp ; iconv -f gbk -t utf-8 {}_temp > {} ; rm -rf {}_temp"

find . -name *.java | xargs -i  sh -c "mv {} {}_temp ; iconv -f gbk -t utf-8 {}_temp > {} ; rm -rf {}_temp"

 for file in `find . -name *.sh` ; do mv ${file} ${file%.sh}.glsl; done
 
 
cd ..