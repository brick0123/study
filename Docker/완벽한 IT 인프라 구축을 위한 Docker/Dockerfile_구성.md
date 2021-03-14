# Dockerfile이란?
Docker에서 인프라 구성을 기술한 파일을 일컫는다. Dockerfile은 Docker상에서 작동시킬 컨테이너의 구성 정보를 기술하기 위한 파일.
</br>
|명령|설명|
|----|----|
|FROM|베이스 이미지 지정|
|RUN|명령 실행|
|CMD|컨테이너 실행 명령|
|LABEL|라벨 설정|
|EXPOSE|포트 익스포트|
|ENV|환경 변수|
|ADD|파일/디렉토리 추가|
|COPY|복사|
|ENTRYPOINT|컨테이너 실행 명령|
|VOLUME|볼륨 마운트|
|USER|사용자 지정|
|WORKDIR|작업 디렉토리|
|ARG|Dockerfile 안의 변수|
|ONBUILD|빌드 완료 후 실행되는 명령|
|STOPSIGNAL|시스템 콜 시그널 설정|
|HEALTHCHECK|컨테이너의 헬스 체크|
|SHELL|기본 쉘 설정|

</br>

### 베이스 이미지 작성 서식
``` shell
FROM [이미지명]
FROM [이미지명]:[태그명] 
FROM [이미지명]@[다이제스트]

- 태그명 생략시 최신 버전 적용.
- 다이제스트란 Dcoker Hub에 업로드 하면 자동으로 부여되는 식별자.
```

### Dockerfile로 부터 이미지 만들기
``` shell
docker build -t [생성할 이미지명]:[태그명] [Dcokerfile의 위치]
```

</br></br>

### 1. Shell 형식으로 기술
명령의 지정을 쉘에서 실행하는 형식으로 기술하는 방법.</br>
ex: Shell 형식의 Run 명령어

``` shell
# Nginx의 설치
RUN -apt install -y nginx
```

이것은 Docker 컨테이너 안에서 /bin/sh -c를 사용하여 명령 했을 때와 똑같이 작동한다. Docker 컨테이너에서 실행할 기본 쉘을 변경하고 싶을 때는 SHELL 명령을 사용한다.

### 2. Exec 형식으로 기술
Shell 형식으로 명령을 기술하면 /bin/sh에서 실행되지만, Exec 형식은 쉘을 경우하지 않고 직접 실행한다. 따라서 명령 인수에 $HOME과 같은 환경 변수를 지정할 수 없다. Exec 형식은 명령을 JSON 배열로 지정한다.
</br>
다른 쉘을 이용하고 싶을 때는 RUN 명령에 쉘의 경로를 지정한 후 실행하고 싶은 명령 지정. ex) /bin/bash에서 apt 명령을 사용하여 Nginx를 설치

``` java
# Nginx의 설치
RUN ["/bin/bash", "-c", "apt install -y nginx"]
```
문자열을 인수로 지정할 때는 홀따옴표를 사용한다.

</br>
여러개 기술하기

``` shell
# 베이스 이미지 설정
FROM ubuntu:latest

# RUN 명령의 실행
RUN echo 안녕하세요 Shell 형식입니다
RUN ["echo", " 안녕하세요 Exex 형식입니다 "]
RUN ["/bin/bash", "-c", "echo '안녕하세요 Exec 형식에서 Bash를 사용 했습니다 ' "]
````

``` shell
# 이미지 레이어
RUN yum -y install httpd php php-mbstring php-pear

# 가독성 향상

RUN yum -y install \
            httpd \
            php \
            php-mbstring \
            php-pear
```

</br>

RUN 명령은 이미지를 작성하기 위해 실행하는 명령을 기술하지만, 이미지를 바탕으로 생성된 컨테이너 안에서 명령을 실행하려면 CMD를 사용한다. Dcokerfile에는 하나의 CMD 명령을 기술할 수 있다. (여러개 지정시 마지막 명령만 유효)

``` shell
CMD [실행하고 싶은 명령]
```

## CMD 명령

### 1. Exec 형식으로 기술.
RUN 명령의 구문과 동일.

``` shell
CMD ["nginx", "-g", "daemon off;"]
```

### 2.Shell 형식으로 기술.
RUN 명령의 구문과 동일.

``` shell
CMD nginx -g 'daemon off;'
```
### 3.ENTRYPOINT 명령의 파라미터로 기술

``` shell
# 베이스 이미지 설명
FROM ubuntu:16.04

# Nginx 설치
RUN apt -y update && apt -y upgrade 
RUN apt -y install nginx

# 포트 지정
EXPOSE 80

# 서버 실행
CMD ["nginx", "-g", "daemon off;"]
```

ENTRYPOINT 명령에서 지정한 명령느 도커파일에서 빌드한 이미지로부터 도커 컨테이너를 시작하기 때문에 Docker container run 명령을 실행했을 때 실행된다.

``` shell
ENTRYPOINT [명령]
```

### 1. Exec 형식

``` shell
ENTRYPOINT ["nginx", "-g", "daemon off;"]
```

### 2. Shell 형식

``` shell
ENTRYPOINT nginx -g 'daemon off;'
```


ENTRYPOINT와 CMD의 차이.
- CMD: dcoker container run 명령이 우선한다.
- ENTRYPOINT: 반드시 컨테이너에서 실행됨. 실행 시 명령 인수를 지정하고 싶을때는 CMD 명령과 조합하여 사용.