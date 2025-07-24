// 대부분 프로그래밍의 시작은 동기화 방식 부터 시작된다.
// result1 = doA();
// result2 = doB(result1);
// result3 = doC(restul2); 인경우 순차적으로 동기화된 코드임
// doA() -> doB() -> doC()의 순서대로 호출됨
// 이때 단점은 doA()가 끝나야 doB()가 실행 됨 -> 동시에 여러작업을 처리 할 수 없음.
//
// ex2) 커피숍이 사장인 1명인 경우 -> 동기화방식
//      커피숍에 사장과 알바가 여러명인 경우 -> 한명은 주문, 나머진 제조 -> 손님은 통보를 받을때까지 대기
// 비동기 핵심은 통보 -> 콜백이라고 한다.
// function doA( callback ) { result1 = callback( result1 ) }
// 위의 코드는 파라미터로 전달 되는 콜백을 내부에서 호출하는데 자바 개발자들에게는 익숙하지 않은 코드임.
// 람다식 부터 사용되기 시작 했으며 자바 스크립트에서 함수는 일급 객체(first-class object)로 일반 객체와 동일한 위상을 가짐
// 파라미터가 되거나 리턴타입이 될 수 있어서 비동기화 코드가 가능함.
// 비동기 방식에서는 콜백을 이용하는 것이 해결책이 되기는 하지만 동기화된 코드에 익숙한 개발자들에게는 조금만 단계가 많아져도 복잡한 코드를 만들어야 함.
//
// 자바 스크립트에서는 Promise라는 개념을 도입해서 비동기 호출을 동기화된 방식으로 작성할 수 있는 문법적인 장치를 만들어 줌 -> Axios
// https://axios-http.com/kr/docs/intro
// Axios는 node.js와 브라우저를 위한 Promise 기반 HTTP 클라이언트 입니다.
// 그것은 동형 입니다(동일한 코드베이스로 브라우저와 node.js에서 실행할 수 있습니다).
// 서버 사이드에서는 네이티브 node.js의 http 모듈을 사용하고, 클라이언트(브라우저)에서는 XMLHttpRequests를 사용합니다.
// <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script> cdn으로 활성화 시킴
// Axios를 이용하면 Ajax를 호출하는 코드를 작성할 때 마치 동기화된 방식 처럼 작성 할 수 있어 자바스크립트 기반으로 하는 프레임 워크(Angular)나 React, Vue에서 많이 사용됨
//
// Axios를 활용해 Ajax를 이용하기 위해서는 댓글 처리가 필요한 화면에 Axios 라이브러리를 추가 해야 함.


// async, await를 같이 이용하면 비동기 처리를 동기화된 코드처럼 작성 가능
// async는 함수 선언시 사용되는데 해당 함수가 비동기 처리를 위한 함수라는 것을 명시하기 위해서 사용


async function get1(bno){
    /* 게시글의 번호를 넘겨 받아서 댓글 리스트가 나오는지...*/

    const result = await axios.get(`/replies/list/${bno}`)
    /*                         @GetMapping  */
    // console.log(result)

    return result;
}

// bno 현재 게시물번호, page 페이지번호, size 페이지당 사이즈, goLast 마지막 페이지 호출 여부
// goLast는 댓글의 경우 한페이지에서 모든 동작이 이루어지므로 새로운 댓글이 등록되어도 화면에는 아무런 변화가 없다는 문제가 발생함.
// 또한 페이징 처리가 되면 새로 등록된 댓글이 마지막 페이지에 있기 때문에 댓글된 결과를 볼 수 없다는 문제가 생김
// goLast 변수를 이용해서 강제적으로 마지막 댓글 페이지를 호출 하려 함.
async function getList({bno, page, size, goLast}){  // p577 쪽
    /*           게시물번호 페이지번호 목록개수 마지막페이지 */
    const result = await axios.get(`/replies/list/${bno}`, {params:{page, size}})

    // 582추가 (댓글 마지막 페이지 호출)
    if(goLast){
        const total = result.data.total // 총댓글수
        const lastPage = parseInt(Math.ceil(total/size))

        return getList({bno:bno, page:lastPage, size:size})
    }

    return result.data
} // 댓글 리스트 함수 종료

async function addReply(replyObj) { // 584 추가
    const response = await axios.post(`/replies/`,replyObj)
    return response.data
} // 댓글 추가용 함수 종료

async function getReply(rno) { // 588 추가
    const response = await axios.get(`/replies/${rno}`)
    return response.data
} // 댓글 자세히보기 함수 종료

async function modifyReply(replyObj) { // 589 추가
    const response = await axios.put(`/replies/${replyObj.rno}`, replyObj)
    return response.data
} // 댓글 수정 함수 종료

async function removeReply(rno) { // 593 추가
    const response = await axios.delete(`/replies/${rno}`)
    return response.data
} // 댓글 삭제 함수 종료
