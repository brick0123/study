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

 </br>

# Flow

![flow](../../assets/mvc/mvc-3.png)</br>
[source](https://terasolunaorg.github.io/guideline/1.0.1.RELEASE/en/Overview/SpringMVCOverview.html)


1. `DispatcherServlet`에서 모든 요청을 받는다. (스프링 부트는 DispatcherServlet을 자동으로 등록하면서 모든 경로에 대해서 매핑한다), 서블릿이 호출되면 DispatcherServicet의 부모인 `FrameworkServlet`의 `service()`메서드를 호출한다. 해당 메서드는 DispatcherServlet에서 오버라이딩 했다. service()를 시작으로 여러 메서드를 실행하며 `doDispatch()`도 여기서 실행된다.
2. DispatcherServlet은 요청 URL을 실행할 수 있는 `Handler`를 `HandlerMapping`을 통해 찾는다.
3. Handler를 실행할 수 있는 `HandlerAdapter`를 조회한 후 Handler 어뎁터를 실행한다.
4. HandlerAdapter는 핸들러(Controller)의 실제 비즈니스 로직을 호출한다.
5. 컨트롤러는 비즈니스 로직을 호출(실행) 하고 결과를 `Model`에 담은 뒤 논리적은 view name을 반환한다.
6. DispatcherServlet은 `viewResolver`에 전달하고 실행하고, 뷰의 논리 이름을 물리 이름으로 변경한 뒤 뷰 객체를 반환한다.
7. `View`를 통하여 Model과 응답을 렌더링한다.

## HandlerMapping

DispatcherServlet에 클라이언트 요청이 들어오면, `HandlerMapping`은 요청과 `handler object` 간의 매핑을 정의하는 인터페이스. (HTTP요청을 처리할 컨트롤러를 찾는다)

``` java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
  ..
  mappedHandler = getHandler(processedRequest);
  ...
}

	protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		if (this.handlerMappings != null) {
			for (HandlerMapping mapping : this.handlerMappings) {
				HandlerExecutionChain handler = mapping.getHandler(request);
				if (handler != null) {
					return handler;
				}
			}
		}
		return null;
	}
```

``` java
public interface HandlerMapping {
	HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
```

### BeanNameUrlHandlerMapping

- 요청 URL과 일치하는 빈의 이름을 반환한다.

### SimpleUrlHandlerMapping
- 빈 인스턴스와 URL 또는 빈 이름과 URL를 직접 매핑할 수 있다.

### RequestMappingHandlerMapping
- 일반적으로 가장 많이 사용되는 것으로 URL과  `@RequestMapping` 의 URL이 일치하는 컨트롤러를 매핑시킨다. 

``` java
@Configuration
public class SimpleUrlHandlerMappingConfig {

    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping
          = new SimpleUrlHandlerMapping();
        
        Map<String, Object> urlMap = new HashMap<>();
        urlMap.put("/simpleUrlWelcome", welcome());
        simpleUrlHandlerMapping.setUrlMap(urlMap);
        
        return simpleUrlHandlerMapping;
    }

    @Bean
    public WelcomeController welcome() {
        return new WelcomeController();
    }
}
```

## HandlerAdapter

`HandlerMapping` 통해 가져온 핸들러를 실행시키는 역할을 한다. `support` 메서드를 통하여 해당 핸들러를 실행할 수 있는 어뎁터를 찾은 뒤, `handle` 메서드를 실행하여 `ModelAndView` 를 반환한다.

``` java
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		...
		HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());	// 핸들러 어댑터 찾기

		mv = ha.handle(processedRequest, response, mappedHandler.getHandler()); // 핸들러 어뎁터를 통해 핸들러 실행하여 ModelAndView 반환
    ..
	}
```

``` java
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		...
		HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());	// 핸들러 어댑터 찾기

		mv = ha.handle(processedRequest, response, mappedHandler.getHandler()); // 핸들러 어뎁터를 통해 핸들러 실행하여 ModelAndView 반환
	}
```

### RequestMappingHandlerAdapter

HandlerAdapter를 구현한 추상 클래스 `AbstractHandlerMethodAdapter` 를 확장시켜서 `@RequestMapping`  어노테이션을 지원하는 어댑터.