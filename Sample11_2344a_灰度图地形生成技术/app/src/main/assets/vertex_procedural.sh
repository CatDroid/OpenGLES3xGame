#version 300 es
uniform mat4 uMVPMatrix; 		//�ܱ任����
in vec3 aPosition;  		//����λ��
in vec2 aTexCoor;    		//������������
out vec2 vTextureCoord;  		//���ڴ��ݸ�ƬԪ��ɫ������������
out float currY;				//���ڴ��ݸ�ƬԪ��ɫ����Y����
void main(){  
   gl_Position = uMVPMatrix * vec4(aPosition,1); 	//�����ܱ任�������˴λ��ƴ˶����λ��
   vTextureCoord = aTexCoor;					//�����յ��������괫�ݸ�ƬԪ��ɫ��
   currY=aPosition.y;						//�������Y���괫�ݸ�ƬԪ��ɫ��
}    
                