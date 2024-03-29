# 역학, 책임, 협력

객체지향 패러다임의 관점에서 핵심은 역할, 책임, 협력이다. 객체지향의 본질은 객체들의 공동체를 창조하는 것이다. 객체지향 설계의 핵심은 협력을 구성하기 위해 객체를 찾고 적절한 책임을 할당하는 과정에서 드러난다.

## 협력

객체지향 시스템은 자율적인 객체들의 공동체다. 협력은 객체지향의 세계에서 기능을 구현할 수 있는 유일한 방법이다. 두 객체 사이의 협력은 하나의 객체가 다른 객체에게 도움을 요청할 때 시작된다. **메세지 전송**은 객체 사이의 협력을 위해 사용할 수 있는 유일한 커뮤니케이션 수단이다. 객체는 다른 객체의 상세한 내부 구현에 직접 접근할 수 없어서 오직 메세지 전송을 통해서만 자신의 요청을 전달할 수 있다.

> 협력이란 어떤 객체가 다른 객체에가 무언가를 요청하는 것이다. 두 객체가 상호작용을 통해 더 큰 책임을 수행한다. 객체 사이의 협력을 설계할 때는 서로 분리된 인스턴스가 아닌 협력하는 파트너로 인식해야된다.
> 

메세지를 수신한 객체는 **메서드**를 실행해 요청에 응답한다. 중요한건 객체가 메세지를 처리할 방법을 스스로 선택하는 점이다. 외부의 객체는 오직 메시지만 전송할 뿐이고 처리는 수신한 객체가 직접 결정한다.

자율적인 객체는 자신의 상태를 직접 관리하고 스스로의 결정에 따라 행동하는 객체다. 객체를 자율적으로 만드는 가장 기본적인 방법은 내부 구현을 **캡슐화**하는 것이다. 캡슐화를 통해 변경에 대한 파급효과를 제한할 수 있기 때문에 자율적인 객체는 변경하기도 쉬워진다.

### 협력이 설계를 위한 문맥을 결정한다

객체란 상태와 행동을 함께 캡슐화하는 실행 단위디/ 객체가 가질 수 있는 상태와 행동을 어떤 기준으로 결정해야할까? 

애플리케이션에서 어떤 객체가 필요하다면 그 이유는 단 하나여야한다. 그 객체가 어떤 협력에 참여하고 있기 때문이다. 그리고 객체가 협력에 참여할 수 있는 이유는 협력에 필요한 적절한 행동을 보유하고 있기 때문이다.
결론적으로 객체의 행동을 결정하는 것은 객체가 참여하고 있는 협력이다. 협력이 바뀌면 객체가 제공해야 하는 행동 역시 바뀌어야한다. 협력은 객체가 필요한 이유와 수행하는 행동의 동기를 제공함.

객체의 행동을 결정하는 것이 협력이라면 객체의 상태를 결정하는 것은 행동이다. 객체의 상태는 그 객체가 행동을 수행하는 데 필요한 정보가 무엇인지로 결정된다. 객체는 자신의 상태를 스스로 결정하고 관리하는 자율적인 존재이기 때문에 객체가 수행하는 행동에 필요한 상태도 함께 가지고 있어야한다.

---

## 책임

### 책임이란

협력에 참여하기 위해 객체가 수행하는 행동을 **책임**이라 한다.

책임이란 객체에 의해 정의되는 응집도 있는 행위의 집합으로, 객체가 유지해야 하는 정보와 수행할 수 있는 행동에 대해 개략적으로 서술한 문장이다.크레이크 라만은  객체의 책임을 크기 “**하는 것**"과 “**아는 것**"의 두 가지 범주로 나누어서 세분화하고 있다.

**하는 것**

- 객체를 생성하거나 계산을 수행하는 등의 스스로 하는 것
- 다른 객체의 행동을 시작시키는 것
- 다른 객체의 활동을 제어하고 조절하는 것

**아는 것**

- 사적인 정보에 관해 아는 것
- 관련된 객체에 관해 아는 것
- 자신이 유도하거나 계산할 수 있는 것에 아는 것

### 책임 할당

자율적인 객체를 만드는 가장 기본적인 방법은 책임을 수행하는 데 필요한 정보를 가장 잘 알고있는 전문가에게 그 책임을 할당하는 것이다. 이를 책임 할당을 위한 information expert(정보 전문가)패턴 이라고 부른다.

객체엑 책임을 할당하기 위해선 먼저 협력이라는 문맥을 정의해야 한다. 협력을 설계하는 출발점은 시스템이 사용자에게 제공하는 기능을 시스템이 담당할 하나의 책임으로 바라보는 것이다. 객체지향 설계는 시스템의 책임을 완료하는 데 필요한 더 작은 책임을 찾아내고 이를 객체들에게 할당하는 반복적인 과정을 통해 모양을 갖춰간다.

### 책임 주도 설계

책임을 찾고 책임을 수행할 적절한 객체를 찾아 책임을 할당하는 방식으로 설계하는 방법을 **책임 주도 설계**라고 부른다. 

- 시스템이 사용자에게 제공해야 하는 기능인 시스템 책임을 파악한다
- 시스템 책임을 더 작은 책임으로 분할한다.
- 분할된 책임을 수행할 수 있는 적절한 객체 또는 역할을 찾아 책임을 할당
- 객체가 책임을 수행하는 도중 다른 객체의 도움이 필요한 경우 이를 책임질 적절한 객체 또는 역할을 찾는다
- 해당 객체 또는 역할에게 책임을 할당함으로써 두 객체가 협렵하게 된다.

책임 주도 설계는 자연스럽게 객체의 구현이 아닌 책임에 집중할 수 있게 한다. 구현에 집중하지 않고 책임에 집중하는 것이 중요한 이유는 유연하고 견고한 객체지향 시스템을 위해 가장 중요한 재료가 바로 책임이기 때문이다.

### 메세지가 객체를 결정한다.

객체에게 책임을 할당하는 데 필요한 메세지를 먼저 식별하고 메시지를 처리할 객체를 나중에 선택했다는 것이 중요하다. 다시 말해 객체가 메시지를 선택하는 것이 아닌 메시지가 객체를 선택하게했다.

메시지가 객체를 선택해야 하는 두 가지 중요한 이유가 있다.

- 객체가 **최소한의 인터페이스(minimal interface)**를 가질 수 있게 된다. 필요한 메시지가 식별될 때까지 객체의 퍼블릭 인터페이스에 어떤 것도 추가하지 않기 때문에 객체는 애플리케이션에 크지도, 작지도 않은 꼭 필요한 크기의 퍼블릭 인터페이스를 가질 수 있다.
- 객체는 충분히 **추상적인 인터페이스(abstract interface)**를 가질 수 있게 된다. 객체의 인터페이스는 무엇을 하는지는 표현해야 하지만, 어떻게 수행하는지는 노출해서는 안 된다. 메시지는 외부의 객체가 요청하는 무언가를 의미하기 때문에 메시지를 먼저 식별하면 무엇을 수행할지에 초점을 맞추는 인터페이스를 얻을 수 있다.

### 행동이 상태를 결정한다

객체가 존재하는 이유는 협력에 참여하기 위해서다. 따라서 객체는 협력에 필요한 행동을 제공해야한다. 객체를 객체답게 만드는 것은 객체의 상태가 아니라 객체가 다른 객체에게 제공하는 행동이다.

보통 가장 큰 실수중 하나는 객체의 행동이 아니라 상태에 초점을 맞추는 것이다. 이런 방식은 객체의 내부 구현이 객체의 퍼블릭 인터페이스에 노출되도록 만들기 때문에 **캡슐화**를 저해한다. 객체의 내부 구현을 변경하면 퍼블릭 인터페이스도 함께 변경되고, 결국 객체에 의존하는 클라이언트로 변경의 영향이 전파된다. 이와 같이 객체의 내부 구현에 초점을 맞춘 설계 방법을 **데이터 주도 설계**라고 부른다.

상태는 단지 객체가 행동을 정상적으로 수행하기 위해 필요한 재료일뿐이다.

---

## 역할

### 역할과 협력

객체가 어떤 특정한 협력 안에서 수행하는 책임의 집합을 **역할**이라고 부른다. 실제로 협력을 리모델링할 때는 특정한 객체가 아니라 역할에게 책임을 할당한다고 생각하는 게 좋다.

### 유연하고 재사용 가능한 협력

역할이 중요한 이유는 역할을 통해 유연하고 재사용 가능한 협력을 얻을 수 있기 때문이다. 

### 객체 대 역할

역할은 객체가 참여할 수 있는 일종의 슬롯이다. 협력에 적합한 책임을 수행하는 대상이 한 종류라면 간단하게 객체로 간주한다. 만일 여러 종류의 객체들이 참여할 수 있다면 역할이라고 부르면 된다.

### 역할과 추상화

추상화가 가지는 두 가지 장점은 협력의 관점에서 역할에도 동일하게 적용될 수 있다.

추상화의 첫번째 장점은 세부 사항에 억눌리지 않고 상위 수준의 정책을 쉽고 간단하게 표현할 수 있다라는 것이다. 추상화를 적절하게 사용하면 불필요한 세부 사항을 생략하고 핵심적인 개념을 강조할 수 있다.

역할이 중요한 이유는 동일한 협력을 수행하는 객체들을 추상화할 수 있기 때문이다.

추상화의 두 번째 장점은 설계를 유연하게 만들 수 있는 것이다. 역할이 다양한 종류의 객체를 끼워 넣을 수 있는 일종의 슬룻이라는 점에 착안하면 쉽게 이해할 수 있을 것이다. 협력 안에서 동일한 책임을 수행하는 객체들은 동일한 역할을 수행하기 때문에 서로 대체 가능하다. 따라서 역할은 다양한 환경에서 다양한 객체들을 수용할 수 있게 해주므로 협력을 유연하게 만든다.

### 배우와 배역

배우는 연극이 상영되는 동안 자신이 연기해야하는 비역의 가면을 쓴다.

- 배역은 연극 배우가 특정 연극에서 연기하는 역할이다
- 배역은 연극이 상영되는 동안에만 존재하는 일시적 개념
- 연극이 끝나면 배우는 배역이라는 역할을 벗어 버리고 원래의 연극 배우로 돌아온다.

배역과 배우 사이의 또 다른 특성은 동일한 배역을 여러 명의 배우들이 연기할 수 있다는 것이다. 배역과 배우 간에는 다음과 같은 추가적인 특성이 존재한다

- 서로 다른 배우들이 동일한 배역을 연기할 수 있다.
- 하나의 배우가 다양한 연극 안에서 다른 배역을 연기할 수 있다.

연극 안에서 배역을 연기하는 배우라는 은유는 협력 안에서 **역할**을 수행하는 객체라는 관점이 가진 입체적인 측면들을 담아낸다. 협력은 연극과 동일하고 코드는 극본과 동일하다.

배우는 연극이 상영될 때 배역이라는 특정한 역할을 연기한다. 객체는 협력이라는 실행 문맥 안에서 특정한 역할을 수행한다. 연극 배우는 연극이 끝나면 자신의 배역을 잊고 원래의 자기 자신을 되찾는다. 객체는 협력이 끝나고 협력에서의 역할을 잊고 원래의 객체로 돌아올 수 있다.