
public class Delegation {
	public static void main(String args[]) {
		E e = new E();
		System.out.println(e.f() + e.g() + e.h() + e.p(1) + e.q(2) + e.r());

		E2 e2 = new E2();
		System.out.println(e2.f() + e2.g() + e2.h() + e2.p(1) + e2.q(2) + e2.r());

		F f = new F();
		System.out.println(f.f() + f.g() + f.h() + f.p(1) + f.q(2) + f.r());

		F2 f2 = new F2();
		System.out.println(f2.f() + f2.g() + f2.h() + f2.p(1) + f2.q(2) + f2.r());

	}
}

abstract class A {
	int a1 = 1;
	int a2 = 2;

	public int f() {
		return a1 + p(100) + q(100);
	}

	protected abstract int p(int m);

	protected abstract int q(int m);
}

class B extends A {
	int b1 = 10;
	int b2 = 20;

	public int g() {
		return f() + this.q(200);
	}

	public int p(int m) {
		return m + b1;
	}

	public int q(int m) {
		return m + b2;
	}
}

abstract class C extends B {
	int c1 = 100;
	int c2 = 200;

	public int r() {
		return f() + g() + h() + c1;
	}

	public int q(int m) {
		return m + a2 + b2 + c2;
	}

	protected abstract int h();
}

class E extends C {
	int e1 = 700;
	int e2 = 800;

	public int q(int m) {
		return super.q(m) + p(m) + c2;
	}

	public int h() {
		return a1 + b1 + c1;
	}

}

class D extends C {
	int d1 = 500;
	int d2 = 600;

	public int r() {
		return f() + g() + h() + c1;
	}

	public int p(int m) {
		return super.p(m) + d2;
	}

	public int h() {
		return a1 + b1 + d1;
	}

}

class F extends D {
	int f1 = 900;
	int f2 = 1000;

	public int q(int m) {
		return p(m) + super.q(m) + a1 + b1 + d1;
	}

	public int h() {
		return a1 + c2 + d1;
	}

}

// ---- Define interfaces IA, IB, IC, ID, IE, and IF ----

interface IA {
	int f();

	int p(int m);

	int q(int m);
}

interface IB extends IA {
	int g();

	int p(int m);

	int q(int m);
}

interface IC extends IB {
	int r();

	int q(int m);

	int h();
}

interface ID extends IC {
	int r();

	int p(int m);

	int h();
}

interface IE extends IC {
	int q(int m);

	int h();
}

interface IF extends ID {
	int q(int m);

	int h();

}

// ---- Complete the definitions of classes ----
// ---- A2, B2, C2, D2, E2, and F2 ----

class A2 implements IA {
	int a1 = 1;
	int a2 = 2;

	IA iaObj;

	public A2(IA obj) {
		iaObj = obj;
	}

	@Override
	public int f() {
		return a1 + iaObj.p(100) + iaObj.q(100);
	}

	@Override
	public int p(int m) {
		return iaObj.p(m);
	}

	@Override
	public int q(int m) {
		return iaObj.q(m);
	}
}

class B2 implements IB {
	int b1 = 10;
	int b2 = 20;
	int a1;
	int a2;

	A2 a2Obj;
	IB ibObj;

	public B2(IB obj) {
		a2Obj = new A2(obj);
		ibObj = obj;
		a1 = a2Obj.a1;
		a2 = a2Obj.a2;
	}

	@Override
	public int f() {
		return a2Obj.f();
	}

	@Override
	public int g() {
		return ibObj.f() + ibObj.q(200);
	}

	@Override
	public int p(int m) {
		return m + b1;
	}

	@Override
	public int q(int m) {
		return m + b2;
	}
}

class C2 implements IC {
	int c1 = 100;
	int c2 = 200;

	int a1;
	int a2;
	int b1;
	int b2;

	IC icObj;
	B2 b2Obj;

	public C2(IC obj) {
		icObj = obj;
		b2Obj = new B2(obj);
		a1 = b2Obj.a1;
		a2 = b2Obj.a2;
		b1 = b2Obj.b1;
		b2 = b2Obj.b2;
	}

	@Override
	public int g() {
		return b2Obj.g();
	}

	@Override
	public int p(int m) {
		return b2Obj.p(m);
	}

	@Override
	public int f() {
		return b2Obj.f();
	}

	@Override
	public int r() {
		return icObj.f() + icObj.g() + icObj.h() + c1;
	}

	@Override
	public int q(int m) {
		return m + a2 + b2 + c2;
	}

	@Override
	public int h() {
		return icObj.h();
	}
}

class E2 implements IE {
	int e1 = 700;
	int e2 = 800;

	int a1;
	int a2;
	int b1;
	int b2;
	int c1;
	int c2;

	C2 c2Obj;

	public E2() {
		c2Obj = new C2(this);
		a1 = c2Obj.a1;
		a2 = c2Obj.a2;
		b1 = c2Obj.b1;
		b2 = c2Obj.b2;
		c1 = c2Obj.c1;
		c2 = c2Obj.c2;
	}

	@Override
	public int r() {
		return c2Obj.r();
	}

	@Override
	public int g() {
		return c2Obj.g();
	}

	@Override
	public int p(int m) {
		return c2Obj.p(m);
	}

	@Override
	public int f() {
		return c2Obj.f();
	}

	@Override
	public int q(int m) {
		return c2Obj.q(m) + p(m) + c2;
	}

	@Override
	public int h() {
		return a1 + b1 + c1;
	}
}

class D2 implements ID {
	int d1 = 500;
	int d2 = 600;
	int a1;
	int a2;
	int b1;
	int b2;
	int c1;
	int c2;

	C2 c2Obj;
	ID idObj;

	public D2(ID obj) {
		idObj = obj;
		c2Obj = new C2(obj);
		a1 = c2Obj.a1;
		a2 = c2Obj.a2;
		b1 = c2Obj.b1;
		b2 = c2Obj.b2;
		c1 = c2Obj.c1;
		c2 = c2Obj.c2;
	}

	@Override
	public int q(int m) {
		return c2Obj.q(m);
	}

	@Override
	public int g() {
		return c2Obj.g();
	}

	@Override
	public int f() {
		return c2Obj.f();
	}

	@Override
	public int r() {
		return idObj.f() + idObj.g() + idObj.h() + c1;
	}

	@Override
	public int p(int m) {
		return c2Obj.p(m) + d2;
	}

	@Override
	public int h() {
		return a1 + b1 + d1;
	}
}

class F2 implements IF {
	int f1 = 900;
	int f2 = 1000;

	int a1;
	int a2;
	int b1;
	int b2;
	int c1;
	int c2;
	int d1;
	int d2;

	D2 d2Obj;

	public F2() {
		d2Obj = new D2(this);
		a1 = d2Obj.a1;
		a2 = d2Obj.a2;
		b1 = d2Obj.b1;
		b2 = d2Obj.b2;
		c1 = d2Obj.c1;
		c2 = d2Obj.c2;
		d1 = d2Obj.d1;
		d2 = d2Obj.d2;
	}

	@Override
	public int r() {
		return d2Obj.r();
	}

	@Override
	public int p(int m) {
		return d2Obj.p(m);
	}

	@Override
	public int g() {
		return d2Obj.g();
	}

	@Override
	public int f() {
		return d2Obj.f();
	}

	@Override
	public int q(int m) {
		return p(m) + d2Obj.q(m) + a1 + b1 + d1;
	}

	@Override
	public int h() {
		return a1 + c2 + d1;
	}
}
