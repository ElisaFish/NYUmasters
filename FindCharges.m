function C = FindCharges(PA,PB,TF)
    w = size(PA,2);
    F = reshape(TF,[],1);
    for i=1:w
        FM(i*w-1:i*w,:) = ForceMatrix(PA(:,i),PB);
    end   
    C = FM \ F;
end