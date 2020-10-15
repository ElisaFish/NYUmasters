with Ada_Integer;
use Ada_Integer;
with Text_IO;
use Text_IO;
with Msort;
use Msort;

procedure Sort (array_range: in out A) is
    integer: low := 1;
    integer: high := length(A);
    integer: mid := (low + high) / 2;
    integer: i := 1;
    integer: j := 1;
    boolean: left := false;
    boolean: right := true;
    --entry Ready(low: in integer);
    entry Left;
    entry Right;

    --task type sort_half(integer: in low, integer: in high);
    
    --task body sort_half(low: in integer, high: in integer) is
    --begin
    --    if length(A) > 1 then
    --        helper := A(low..high);
    --        Sort(helper);
    --    end if;
    --    Sort.Ready(low);
    --end sort_half;

    --sort_left : sort_half(low, mid);
    --sort_right : sort_half(mid+1, high);

    --task sort_left(integer: in low, integer: in mid);

    task sort_left is
    begin
        if length(A) > 1 then
            helper := A(low..mid);
            Sort(helper);
        end if;
    end sort_left;

    --task sort_right(integer: in mid, integer: in high);

    task sort_right is
    begin
        if length(A) > 1 then
            helper := A(mid+1 .. high);
            Sort(helper);
        end if;
    end sort_right;

begin -- Sort
    -- merge array here with merge sort
    --accept Ready(low) do
    --    if low = 1 then
    --        left := true;
    --    elsif low = mid+1 then
    --        right := true;
    --    end if;
    --end Ready;

    loop
       select
        accept Left do
            left := true;
        end Left;
       or
        accept Right do
            right := true;
        end Right;
    end loop;
    

    if left and right then
        for k in array_index loop
            if left(i) <= right(j) then
                A(k) := left(i);
                i := i + 1;
            else
                A(k) := right(j);
                j := j + 1;
            --k := k + 1;
            end if;
        end loop;
    end if;
end Sort;