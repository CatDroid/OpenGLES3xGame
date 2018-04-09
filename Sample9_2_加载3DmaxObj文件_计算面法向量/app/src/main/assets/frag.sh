#version 300 es
precision mediump float;
in vec4 ambient;
in vec4 diffuse;
in vec4 specular;
in vec3 worldcoord;
out vec4 fragColor;//输出到的片元颜色
void main()                         
{
    vec4 finalColor ;
    float y = worldcoord.y;
    y = mod( y + 100.0 , 4.0) ; // +100 避免负数
    if ( y > 1.8){
        finalColor = vec4(0.678,0.231,0.129,0);
    }else{
        finalColor = vec4(0.763,0.657,0.614,0);
    }
    //vec4 finalColor=vec4(0.9,0.9,0.9,1.0);
    fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;//给此片元颜色值
}   