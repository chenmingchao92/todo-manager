# todo-manager

1.目前只完成了第一阶段，但是从开始开发，到第一阶段完成，前前后后花了有20个小时了快。因为不停地一边写一边改，一边写一遍改。

2.我发现取一个好的名字，真的是太难了，简直比功能开发还难

3.我知道可以用 spring shell  但是 我还是想减少对框架的依赖，多体现下自己写的代码，所以我就没用spring shell，目前只用了 spring的基础功能。估计这也是导致开发时间过长的原因

4.在对命令输入进行处理的时候，我选择了模仿spring mvc 分发器的功能，实现一个简易的分发器，就是 我定义好注解，表明每个注解是干嘛的，然后用反射的方式将每个命令需要调用的某个类中的某个方法，放入一个路由表中（也就是 Map<String,Object>)，这样就能针对命令调用对应的方法，可以做到 业务功能，和命令执行的解耦，不用写一堆if语句（我也是思考了好久，才确定到用这种方式解耦）

5.我思考过 怎么样才能处理 任意顺序的 参数选项和正文，比如 todo add  content --add 或todo add --add content 都能正确识别，想过用 ''将正文括起来，或者用一些特殊字符将正文括起来,以和参数选项进行区别，例如模仿 soap的 <![CDATA[]]>格式，但是最终没采用，还是用了 按照顺序处理格式，--的表示为参数的方式进行处理的，不以--开头的为正文。

6.一开始写反射获取注解的时候，需要扫描包，网上找了好久也没有找到好的方式，后来用了一种 ClassLoader.getResource("") 方式进行一顿复杂的处理，然后发现打成jar包了 居然不可用！最终还是采用的 通过spring提供的工具，来获取已经被他管理的bean的方式 来完成的（为什么是最终的，因为一开始没想到，太菜了）

7.我在实现功能的时候，也是一边写，一遍对已经实现的代码的结构进行调整，希望能“写好”，但是挺茫然的，我也不知道这么调整对不对，这么分解对不对。心里没有一个尺寸，只能根据自己的感觉走，但是众所周知，经验不足的人，人的感觉一般都不准（这是老师说过的话）。。。

8.在处理 todo done 这个功能的时候，需要修改指定文件中的某一行，我想了很久也没想到更好的注意，只能把文件的内容都读出来，然后修改指定的内容，在重新写入进去，我凭感觉 觉得这样不行（比如文件很大的时候）但是没想到更好的解决方案。

9.我真的很认真的写了。。。

10.我想当一个能 “写好”代码的程序员，着就是我现在的**目标**。