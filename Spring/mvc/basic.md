# Servlet

### 특징

- request마다 쓰레드를 사용한다.
- HttpServlet을 상속받아 구현한다.

### 생명주기
- init(): 서블릿 인스턴스를 생성한다. 
  - 최초 요청을 받았을 때 한번 초기하며 그 다음부터는 생략된다.
- service(): 실제 코드가 수행되는 로직. HTTP 요청에 해당하는 메서드를 호출한다. 
  - ex) doGet(), doPost()
- destroy(): 해당 서블릿이 메모리에서 내릴때 호출한다.


### 서블릿 컨테이너

- 톰캣처럼 서블릿을 지원하는 WAS
- 서블릿 컨테이너는 서블릿의 생명주기를 관리.
- 서블릿 객체는 `싱글톤`으로 관리
- 동시 요청을 위한 멀티스레드 지원

# MVC Pattern

### 개요

MVC 패턴을 적용하기 전 Servletm JSP는 비즈니스 로직과 뷰 렌더링을 한 곳에서 모두 처리하였다 그러다보니 역할이 너무 과해지고 이는 심각한 유지보수를 초리하게 된다. </br>
또한 비즈니스 로직과 뷰 렌더링을 예로 들면 이 둘의 변경 라이프 사이클이 다른데 하나의 코드로 관리하게 되는 문제 또한 발생한다.

</br>

###  Model View Controller

![mvc](../../assets/mvc/mvc-1.png)[source](https://developer.mozilla.org/ko/docs/Glossary/MVC)

</br>

- Model: 데이터와 관련된 부분이다. View에 출력할 데이터를 담는데, 이로인해 Controller와 View 사이에서 결합성을 낮추고 각자 역할에 집중할 수 있게된다. 
- View: 모델에 담겨있는 데이터를 이용하여 화면 출력을 담당한다.
- Controller: Http 요청을 받아서, 데이터를 조회하거나 비즈니스 로직을 호출하여 뷰에 전달한 데이터를 모델에 담는다.

### 한계

각자 역할을 분리하는 데 성공했지만, 여전히 중복, 불필요한 코드가 많이 남아있다. </br>
View로 이동하는 호출 코드를 매번 실행해야하며, 경로도 설정해줘야한다. 뷰 템플릿을 변경하게 될 경우 전체 코드를 다 변경해야될 수도 있다.

``` java
// 테스트를 작성하기도 까다롭고, response는 잘 사용하지도 않는데 계속 갖고있어야한다.
HttpServletRequest request, HttpServletResponse response
```
</br>
애플리케이션이 커지면서 컨트롤러에서 공통으로 처리해야될 부분들이 생길 것인데, 이 부분을 해결하기 힘들다.
만일 메서드로 따로 만든다고 하더라도, 메서드를 호출하는 것도 결국 중복이 된다.
</br>
단순 MVC 패턴만 이용하면 공통적으로 무언가를 처리하기 힘들다는 점이 있다. 이 문제점을 해결하려면 컨트롤러 호출 전에 공통 기능을 처리해야 하는데, 

`Front Controller` 패턴을 도입하여 이런 문제점을 해결할 수 있다. 스프링 MVC 핵심도 여기에 있다.

# Front Controller Pattern
![front](../../assets/mvc/mvc-2.png)  </br>
[source](https://developer.ucsd.edu/develop/user-interface-3/applying-mvc.html)

</br>

웹 서버에 요청이 들어오면 기존의 방식에서는 컨트롤러가 각각 요청을 받아서 독립적으로 실행되었다. 이렇게 수 많은 컨트롤러들이 요청을 독립적으로 실행되면 공통 처리하는 게 어렵기 떄문에 중복이 많이 발생한다. </br>
`FrontController`패턴은 모든 요청을 **한 곳**에서 일괄적으로 처리해주는 것이다. 프론트 컨트롤러 하나로 클라이언트의 요청을 받아서, 요청에 맞는 알맞은 컨트롤러를 찾아서 호출해준다. 입구를 하나를 이용하여 클라이언트의 요청을 받으니 공통 처리도 가능해진다. 스프링 웹 MVC의 `DispatcherServlet`이 FrontController 패턴으로 구현되어있다.