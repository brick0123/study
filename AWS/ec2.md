## AWS EC2 HOSTNAME 변경

### ec2 접속

``` shell
1. sudo hostnamectl set-hostname hostname
```


``` shell
2. sudo vim /etc/sysconfig/network
HOSTNAME=hostname
```

``` shell
3. sudo reboot
```

### 추가 작업

``` shell
sudo vim /etc/hosts
```
방금 등록한 hostname을 등록한다
``` shell
127.0.0.1 hostname
```

### 확인

``` shell
curl hostname
```

### 결과
```  shell
Could not resolve host ~  // 등록 실패
Fail to connect to ~      // 등록 성공
```