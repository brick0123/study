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
